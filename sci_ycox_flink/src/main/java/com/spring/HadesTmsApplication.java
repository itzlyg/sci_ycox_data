package com.spring;

import com.ccb.flink.bean.SinkResult;
import com.ccb.flink.util.RedisSinkKey;
import com.ccb.flink.util.YamlConfigReader;
import com.spring.sink.SimpleSink;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.redis.RedisSink;
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisPoolConfig;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommand;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommandDescription;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisMapper;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;

//@SpringBootApplication
public class HadesTmsApplication implements CommandLineRunner {

//    public static void main(String[] args) {
//        SpringApplication application = new SpringApplication(HadesTmsApplication.class);
//        application.setBannerMode(Banner.Mode.OFF);
//        application.run(args);
//    }

    @Override
    public void run(String... args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.enableCheckpointing(5000);
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        env.setParallelism(9);
        List<SinkResult> list = new ArrayList<>();
        SinkResult sinkResult = new SinkResult();
        sinkResult.setAppid("444");
        sinkResult.setContext("cdsfsd");
        sinkResult.setNum(1);
        list.add(sinkResult);
        sinkResult = new SinkResult();
        sinkResult.setAppid("444");
        sinkResult.setContext("cdsfsdwew");
        sinkResult.setNum(10);
        list.add(sinkResult);
        DataStreamSource<Tuple3<String, String, Integer>> ips = env.fromElements(
                Tuple3.of("u_1", "sid_1", 1),
                Tuple3.of("u_1", "sid_2", 1),
                Tuple3.of("u_2", "sid_1", 1),
                Tuple3.of("u_2", "sid_2", 1),
                Tuple3.of("u_1", "sid_1", 1),
                Tuple3.of("u_1", "sid_3", 1),
                Tuple3.of("u_1", "sid_1", 1)
        );

        FlinkJedisPoolConfig.Builder redisBuilder = YamlConfigReader.getPoolBuild();
        FlinkJedisPoolConfig cig = redisBuilder.setDatabase(10).build();
        ips.keyBy(1).reduce((m, n) -> Tuple3.of(m.f0, m.f1, m.f2 + n.f2))
                .addSink(new SimpleSink());
//        .addSink(new RedisSink<>(cig, new Sink()));
// 此处省略处理逻辑
//        ips.addSink(new RedisSink<>(cig, new Sink()));
        env.execute("job name");
    }

    private static class Sink  implements RedisMapper<Tuple3<String, String, Integer>> {

        private static final long serialVersionUID = -5585061738635304329L;


        @Override
        public RedisCommandDescription getCommandDescription() {
            return new RedisCommandDescription(RedisCommand.SET, null);
        }

        @Override
        public String getKeyFromData(Tuple3<String, String, Integer> t) {
            System.out.println(t.f0);
            return t.f0;
        }

        @Override
        public String getValueFromData(Tuple3<String, String, Integer> t) {
            return t.f2 + "";
        }
    }

}
