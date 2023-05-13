package com.sci.ycox.flink.operator;

import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks;
import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.apache.flink.streaming.api.windowing.time.Time;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * https://blog.csdn.net/xsdxs/article/details/82415450
 */

public class TimeWindowDemo {


    public static void main(String[] args) throws java.lang.Exception {
        long delay = 5100L;
        int windowSize = 15;

        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // 设置数据源
        env.setParallelism(1);
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        DataStream<Tuple3<String, String, Long>> dataStream = env.addSource(new TimeWindowDemo.DataSource()).name("Demo Source");

        // 设置水位线
        DataStream<Tuple3<String, String, Long>> watermark = dataStream.assignTimestampsAndWatermarks(new AssignerWithPeriodicWatermarks<Tuple3<String, String, Long>>() {
            /**
			 * 
			 */
			private static final long serialVersionUID = -1571226930241417933L;
			private final long maxOutOfOrderness = delay;
            private long currentMaxTimestamp = 0L;

            @Override
            public Watermark getCurrentWatermark() {
                return new Watermark(currentMaxTimestamp - maxOutOfOrderness);
            }

            @Override
            public long extractTimestamp(Tuple3<String, String, Long> element, long previousElementTimestamp) {
                long timestamp = element.f2;
                System.out.println(element.f1 + " -> " + timestamp + " -> " + toLdt(timestamp));
                currentMaxTimestamp = Math.max(timestamp, currentMaxTimestamp);
                return timestamp;
            }
        });

        // 窗口函数进行处理
        DataStream<Tuple3<String, String, Long>> resStream = watermark.keyBy(0).timeWindow(Time.seconds(windowSize)).reduce(
                (v1, v2) -> Tuple3.of(v1.f0, v1.f1 + "-" + v2.f1, 1L)
        );

        resStream.print();

        env.execute("event time demo");
    }
    public static LocalDateTime toLdt(long milli){
        return Instant.ofEpochMilli(milli).atZone(ZoneOffset.ofHours(8)).toLocalDateTime();
    }

    private static class DataSource extends RichParallelSourceFunction<Tuple3<String, String, Long>> {
        /**
		 * 
		 */
		private static final long serialVersionUID = 3283771640961781604L;
		private volatile boolean running = true;

        @Override
        public void run(SourceContext<Tuple3<String, String, Long>> ctx) throws InterruptedException {
            Tuple3[] elements = new Tuple3[]{
                    Tuple3.of("a", "1", 1000000050000L),
                    Tuple3.of("a", "2", 1000000054000L),// 4
                    Tuple3.of("a", "3", 1000000079900L),// 25.9 过了时间窗口 直接打印
                    Tuple3.of("a", "4", 1000000115000L),// 36
                    Tuple3.of("b", "5", 1000000100000L),// -15
                    Tuple3.of("b", "6", 1000000108000L)// 8
            };

            int count = 0;
            while (running && count < elements.length) {
                ctx.collect(new Tuple3<>((String) elements[count].f0, (String) elements[count].f1, (Long) elements[count].f2));
                count++;
                Thread.sleep(1000);
            }
        }

        @Override
        public void cancel() {
            running = false;
        }
    }
}
