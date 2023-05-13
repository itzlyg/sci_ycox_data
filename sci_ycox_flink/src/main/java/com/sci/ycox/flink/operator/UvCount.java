package com.sci.ycox.flink.operator;

import com.sci.ycox.flink.bean.UrlVisitBy;
import com.sci.ycox.flink.bean.VisitEvent;
import com.sci.ycox.flink.util.JsonUtil;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.SlidingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumerBase;
import org.apache.flink.table.api.java.StreamTableEnvironment;
import org.apache.flink.util.Collector;

import java.time.Instant;
import java.util.Properties;

public class UvCount {
    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // 非常关键，一定要设置启动检查点！！
        env.enableCheckpointing(5000);
        env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime);
        // 创建注册表环境
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);
        Properties props = new Properties();
        props.setProperty("bootstrap.servers", "127.0.0.1:8802");

        FlinkKafkaConsumer<String> consumer = new FlinkKafkaConsumer<>("flinktest", new SimpleStringSchema(), props);

        DataStream<String> stream = env.addSource(consumer);

        long maxEventDelay = 3500;
        BoundedOutOfOrdernessTimestampExtractor<String> assigner = new BoundedOutOfOrdernessTimestampExtractor<String>(Time.milliseconds(maxEventDelay)) {
            @Override
            public long extractTimestamp(String element) {
                VisitEvent visitEvent = null;
                try {
                    visitEvent = JsonUtil.toPojo(element, VisitEvent.class);
                    return visitEvent.getVisitTime();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return Instant.now().toEpochMilli();
            }
        };

        int[] arr = {0,2};
        FlinkKafkaConsumerBase<String> consumerWithEventTime = consumer.assignTimestampsAndWatermarks(assigner);
        TypeInformation<Tuple3<String, VisitEvent, String>> typeInformation = TypeInformation.of(new TypeHint<Tuple3<String, VisitEvent, String>>() {});
        DataStreamSource<String> dataStreamByEventTime = env.addSource(consumerWithEventTime);
        SingleOutputStreamOperator<UrlVisitBy> uvCounter = dataStreamByEventTime
                .map(str -> JsonUtil.toPojo(str,VisitEvent.class))
                .map(visitEvent-> new Tuple3<>(visitEvent.getVisitUrl(), visitEvent,visitEvent.getVisitUserId()))
                .returns(typeInformation)
                .keyBy(arr)
                .window(SlidingProcessingTimeWindows.of(Time.minutes(5), Time.minutes(1),Time.hours(-8)))
                .allowedLateness(Time.minutes(1))
                .process(new ProcessWindowFunction<Tuple3<String, VisitEvent, String>, UrlVisitBy, Tuple, TimeWindow>() {
                    @Override
                    public void process(Tuple tuple, Context context, Iterable<Tuple3<String, VisitEvent, String>> elements, Collector<UrlVisitBy> out) throws Exception {
                        long count = 0;
                        Tuple2<String,String> tuple2 = null;
                        if (tuple instanceof Tuple2){
                            tuple2 = (Tuple2) tuple;
                        }
                        for (Tuple3<String, VisitEvent, String> element : elements) {
                            count++;
                        }
                        TimeWindow window = context.window();
                        out.collect(new UrlVisitBy(window.getStart(),window.getEnd(),tuple2.f0,count,tuple2.f1));
                    }
                });
        uvCounter.print();
    }

}
