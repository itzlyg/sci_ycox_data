package com.sci.ycox.flink.operator;

import com.alibaba.fastjson.JSONObject;
import com.sci.ycox.flink.bean.ResultInfo;
import com.sci.ycox.flink.bean.SourceEntity;
import com.sci.ycox.flink.bean.SourceOut;
import com.sci.ycox.flink.enume.AppDbindex;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.AssignerWithPunctuatedWatermarks;
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.redis.RedisSink;
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisPoolConfig;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommand;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommandDescription;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisMapper;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.java.StreamTableEnvironment;

import javax.annotation.Nullable;
import java.text.SimpleDateFormat;
import java.util.Properties;

/**
 * EventTime sql的例子
 * 根据事件的时间戳ptime 作为EnevtTime
 */
public class EventTimeExamlpe {

    private static SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm");
    /**
     * AssignerWithPeriodicWatermarks，这种方式会定时提取更新wartermark
     * AssignerWithPunctuatedWatermarks  伴随event的到来就提取watermark，就是每一个event到来的时候，就会提取一次Watermark，
     * 这样的方式当然设置watermark更为精准，但是当数据量大的时候，频繁的更新wartermark会比较影响性能。通常情况下采用定时提取就足够了
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.enableCheckpointing(5000); // 非常关键，一定要设置启动检查点！！
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        env.setParallelism(10);
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);
        Properties props = new Properties();
        props.setProperty("bootstrap.servers", "127.0.0.1:8802");
        FlinkJedisPoolConfig.Builder redisBuilder = new FlinkJedisPoolConfig.Builder().setHost("127.0.0.1");
        for (AppDbindex appDbindex : AppDbindex.values()){
            consumer(appDbindex, props, redisBuilder, env, tableEnv);
        }
        env.execute("Flink-Kafka demo");
    }

    /**
     * 分区读取数据写数据到对应的redis db中
     * @param appDbindex app_id---redisDB
     * @param props kafka配置
     * @param redisBuilder redis连接
     * @param env 环境
     * @param tableEnv table环境
     */
    private static void consumer (AppDbindex appDbindex, Properties props, FlinkJedisPoolConfig.Builder redisBuilder, StreamExecutionEnvironment env , StreamTableEnvironment tableEnv){
        // 读取kafka的数据
        FlinkKafkaConsumer<String> consumer = new FlinkKafkaConsumer<>(appDbindex.getAppId(), new SimpleStringSchema(), props);
        DataStream<String> stream = env.addSource(consumer);
        // appId uid ip url ptime
        DataStream<SourceEntity> map = stream.map(json -> JSONObject.parseObject(json, SourceOut.class).getArgs());
        FlinkJedisPoolConfig redisConfig = redisBuilder.setDatabase(appDbindex.getIndex()).build();
        // 设置水位线 按照事件提取的方式 把窗口时间里的数据作为新的流对象
        DataStream<SourceEntity> watermark = map.assignTimestampsAndWatermarks(new AssignerWithPunctuatedWatermarksExtractor());
        String tableName = appDbindex.getAppId() + "_source_time";
        tableEnv.registerDataStream(tableName, watermark, "appId,pvi,ip,url,ptime.rowtime");
        StringBuffer sql = new StringBuffer("");

        sql.append("  SELECT TUMBLE_END(ptime, INTERVAL '60' SECOND) as ptime, appId, '' name,  pvi,count(pvi) as pvcount ");
        sql.append("FROM ").append(tableName);
//        sql.append("WHERE appId = 'APP_A0001' ");
        sql.append(" GROUP BY TUMBLE(ptime, INTERVAL '60' SECOND),appId, pvi ");
        Table query = tableEnv.sqlQuery(sql.toString());
        DataStream<ResultInfo> strea = tableEnv.toAppendStream(query, ResultInfo.class);
        strea.addSink(new RedisSink<ResultInfo>(redisConfig, new RedisSinkExample()));
        strea.map(o -> o.toString()).print();
    }




    public static class RedisSinkExample implements RedisMapper<ResultInfo> {

        /**
		 * 
		 */
		private static final long serialVersionUID = -5358621014326496695L;

		@Override
        public RedisCommandDescription getCommandDescription() {
            /** LPUSH 最新的数据在上面 */
            return new RedisCommandDescription(RedisCommand.LPUSH, null);
        }

        @Override
        public String getKeyFromData(ResultInfo info) {
            return info.getAppId() + ":" + info.getPvi();
        }

        @Override
        public String getValueFromData(ResultInfo info) {
            return JSONObject.toJSONString(info);
        }
    }

    /**
     * 事件提取
     */
    public static class AssignerWithPunctuatedWatermarksExtractor implements AssignerWithPunctuatedWatermarks<SourceEntity> {
        /**
		 * 
		 */
		private static final long serialVersionUID = -8545223326666410866L;

		/** 延迟时间 */
		private final long maxOutOfOrderness = 3500; // 3.5 seconds

        /** 最大事件时间 */
        private long currentMaxTimestamp;

        @Nullable
        @Override
        public Watermark checkAndGetNextWatermark(SourceEntity lastElement, long extractedTimestamp) {
            //return new Watermark(System.currentTimeMillis() - maxOutOfOrderness);
            return new Watermark(currentMaxTimestamp - maxOutOfOrderness);
        }

        @Override
        public long extractTimestamp(SourceEntity element, long previousElementTimestamp) {
            // return element.f4;
            long timestamp = element.getRandom();
            currentMaxTimestamp = Math.max(timestamp, currentMaxTimestamp);
            return timestamp;
        }
    }

    /**
     * AssignerWithPeriodicWatermarks 定时读取的方式
     */
    public static class AssignerWithPeriodicWatermarksExtractor extends BoundedOutOfOrdernessTimestampExtractor<SourceEntity> {

        /**
		 * 
		 */
		private static final long serialVersionUID = -3211076389198922505L;
		public AssignerWithPeriodicWatermarksExtractor(){
            super(Time.seconds(10));
        }
        @Override
        public long extractTimestamp(SourceEntity element) {
            return element.getRandom();
        }

    }
}
