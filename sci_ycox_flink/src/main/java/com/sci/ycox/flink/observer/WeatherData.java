package com.sci.ycox.flink.observer;

import com.sci.ycox.flink.enume.AppDbindex;
import com.sci.ycox.flink.observer.inter.Observer;
import com.sci.ycox.flink.observer.inter.Subject;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisPoolConfig;
import org.apache.flink.table.api.java.StreamTableEnvironment;

import java.util.ArrayList;
import java.util.List;

public class WeatherData implements Subject {

    /**
	 * 
	 */
	private static final long serialVersionUID = -7466130910925479420L;

	private StreamExecutionEnvironment env;

    private FlinkKafkaConsumer<String> consumer;

    private StreamTableEnvironment tableEnv;

    private AppDbindex appDbindex;

    private FlinkJedisPoolConfig.Builder redisBuilder;

    //观察者列表
    private List<Observer> list;

    public WeatherData(){
        list = new ArrayList<Observer>();
    }

    @Override
    public void addObserver(Observer observer) {
        list.add(observer);
    }

    @Override
    public void deleteObserver(Observer observer) {
        int i;
        if((i = list.indexOf(observer)) != -1) {
            this.list.remove(i);
        }
    }

    @Override
    public void notifyObservers() {
        for(Observer observer : this.list) {
            observer.excute();
        }

    }

    public void init (StreamExecutionEnvironment env, FlinkKafkaConsumer<String> consumer){
        this.env = env;
        this.consumer = consumer;
    }

    public StreamTableEnvironment getTableEnv() {
        return tableEnv;
    }

    public void setTableEnv(StreamTableEnvironment tableEnv) {
        this.tableEnv = tableEnv;
    }

    public AppDbindex getAppDbindex() {
        return appDbindex;
    }

    public void setAppDbindex(AppDbindex appDbindex) {
        this.appDbindex = appDbindex;
    }

    public StreamExecutionEnvironment getEnv() {
        return env;
    }

    public FlinkKafkaConsumer<String> getConsumer() {
        return consumer;
    }

    public FlinkJedisPoolConfig.Builder getRedisBuilder() {
        return redisBuilder;
    }

    public void setRedisBuilder(FlinkJedisPoolConfig.Builder redisBuilder) {
        this.redisBuilder = redisBuilder;
    }
}
