package com.sci.ycox.flink.main;

import com.sci.ycox.flink.enume.AppDbindex;
import com.sci.ycox.flink.observer.WeatherData;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisPoolConfig;
import org.apache.flink.table.api.java.StreamTableEnvironment;

import java.util.Properties;

public class ReadData {

    public static void main(String[] args) throws Exception {
       System.out.println(System.currentTimeMillis());
    }

    public static void flink ()throws Exception{
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // 非常关键，一定要设置启动检查点！！
        env.enableCheckpointing(5000);
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        env.setParallelism(10);
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);
        Properties props = new Properties();
        props.setProperty("bootstrap.servers", "127.0.0.1:8802");
        FlinkJedisPoolConfig.Builder redisBuilder = new FlinkJedisPoolConfig.Builder().setHost("127.0.0.1");
        FlinkKafkaConsumer<String> consumer = null;
        WeatherData data = null;
        SimpleStringSchema schema = new SimpleStringSchema();
//        UvForSencond sencond = null;
//        UvForMinute minute = null;
        for (AppDbindex appDbindex : AppDbindex.values()){
            data = new WeatherData();
            consumer = new FlinkKafkaConsumer<>(appDbindex.getAppId(), schema, props);
            data.init(env, consumer);
            data.setAppDbindex(appDbindex);
            data.setTableEnv(tableEnv);
            data.setRedisBuilder(redisBuilder);
//            sencond = new UvForSencond(data);
            //minute = new UvForMinute(data);
            data.notifyObservers();
        }
        env.execute("Flink");
    }
}
