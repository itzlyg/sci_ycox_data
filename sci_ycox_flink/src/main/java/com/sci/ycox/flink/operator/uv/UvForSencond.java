package com.sci.ycox.flink.operator.uv;

import com.alibaba.fastjson.JSONObject;
import com.sci.ycox.flink.bean.ResultInfo;
import com.sci.ycox.flink.bean.SourceEntity;
import com.sci.ycox.flink.enume.AppDbindex;
import com.sci.ycox.flink.observer.WeatherData;
import com.sci.ycox.flink.observer.inter.Observer;
import com.sci.ycox.flink.operator.AssignerWithPeriodicWatermarkSeconds;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.connectors.redis.RedisSink;
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisPoolConfig;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommand;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommandDescription;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisMapper;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.java.StreamTableEnvironment;

import java.text.SimpleDateFormat;

public class UvForSencond implements Observer  {

    /**
	 * 
	 */
	private static final long serialVersionUID = -8260482150551928287L;
	private SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
    private WeatherData data;

    public UvForSencond(WeatherData subject){
        this.data = subject;
        this.data.addObserver(this);
    }

    @Override
    public void excute() {
        StreamTableEnvironment tableEnv = data.getTableEnv();
        DataStream<String> stream = data.getEnv().addSource(data.getConsumer());
        // appId uid ip url ptime
        DataStream<SourceEntity> map = stream.map(json -> JSONObject.parseObject(json, SourceEntity.class));
        AppDbindex appDbindex = data.getAppDbindex();
        FlinkJedisPoolConfig.Builder redisBuilder = data.getRedisBuilder();
        FlinkJedisPoolConfig redisConfig = redisBuilder.setDatabase(appDbindex.getIndex()).build();
        // 设置水位线 按照事件提取的方式 把窗口时间里的数据作为新的流对象
        DataStream<SourceEntity> watermark = map.assignTimestampsAndWatermarks(new AssignerWithPeriodicWatermarkSeconds(1));
        String tableName = appDbindex.getAppId() + "_source_uv_sencond";
        tableEnv.registerDataStream(tableName, watermark, "r2,pvi,ip,url,random.rowtime");
        StringBuffer sql = new StringBuffer("");

        sql.append("SELECT TUMBLE_END(random, INTERVAL '5' SECOND) as ptime, '");
        sql.append(appDbindex.getAppId());
        sql.append("' appId, '' name, pvi, count(pvi) as pvcount ");
        sql.append("FROM ").append(tableName);
        sql.append(" GROUP BY TUMBLE(random, INTERVAL '5' SECOND), pvi ");
        Table query = tableEnv.sqlQuery(sql.toString());
        DataStream<ResultInfo> strea = tableEnv.toAppendStream(query, ResultInfo.class);
        strea.addSink(new RedisSink<ResultInfo>(redisConfig, new UvRedisSink()));
        strea.map(o -> o.toString()).print();
    }

    private class UvRedisSink implements RedisMapper<ResultInfo> {

        /**
		 * 
		 */
		private static final long serialVersionUID = 8671233282347683534L;

		@Override
        public RedisCommandDescription getCommandDescription() {
            /** LPUSH 最新的数据在上面 */
            return new RedisCommandDescription(RedisCommand.PFADD, null);
        }

        @Override
        public String getKeyFromData(ResultInfo info) {
            return format.format(info.getPtime()) + "UV";
        }

        @Override
        public String getValueFromData(ResultInfo info) {
            // 实时的计算
            return info.getPvi();
        }
    }
}
