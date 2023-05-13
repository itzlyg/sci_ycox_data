package com.sci.ycox.flink.operator;

import com.sci.ycox.flink.bean.SourceEntity;
import com.sci.ycox.flink.bean.SourceOut;
import com.sci.ycox.flink.enume.AppDbindex;
import com.sci.ycox.flink.util.JsonUtil;
import com.sci.ycox.flink.util.Utc2Local;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisPoolConfig;
import org.apache.flink.table.api.java.StreamTableEnvironment;
import org.apache.flink.table.functions.ScalarFunction;

import java.util.Properties;

public class UvCountExample {

    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // 非常关键，一定要设置启动检查点！！
        env.enableCheckpointing(5000);
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        ScalarFunction scalarFunction = null;
        env.setParallelism(10);
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);
        tableEnv.registerFunction("utc2local", new Utc2Local());
        Properties props = new Properties();
        props.setProperty("bootstrap.servers", "127.0.0.1:8802");
        FlinkJedisPoolConfig.Builder redisBuilder = new FlinkJedisPoolConfig.Builder().setHost("127.0.0.1");
        FlinkKafkaConsumer<String> consumer;
        DataStream<SourceEntity> sources = null;
        FlinkJedisPoolConfig redisConfig = null;
        SimpleStringSchema schema = new SimpleStringSchema();
        for (AppDbindex appDbindex : AppDbindex.values()){
            // 读取kafka topic的数据
            consumer = new FlinkKafkaConsumer<>(appDbindex.getAppId(), schema, props);
            // appId uid ip url ptime
            sources = env.addSource(consumer).map(json -> JsonUtil.toPojo(json, SourceOut.class).getArgs());
            redisConfig = redisBuilder.setDatabase(appDbindex.getIndex()).build();
        }
        env.execute("Uv 5s Count");
    }

}

