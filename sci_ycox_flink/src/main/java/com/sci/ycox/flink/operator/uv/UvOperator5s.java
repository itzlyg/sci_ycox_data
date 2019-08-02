package com.sci.ycox.flink.operator.uv;

import com.sci.ycox.flink.bean.SourceEntity;
import com.sci.ycox.flink.enume.AppDbindex;
import com.sci.ycox.flink.operator.AssignerWithPeriodicWatermarkSeconds;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.connectors.redis.RedisSink;
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisPoolConfig;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommand;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommandDescription;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisMapper;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.java.StreamTableEnvironment;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class UvOperator5s {

    private static SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");

    /**
     * 分区读取数据写数据到对应的redis db中
     * @param appDbindex app_id---redisDB
     * @param tableEnv table环境
     */
    public static void excute (AppDbindex appDbindex, DataStream<SourceEntity> sources, FlinkJedisPoolConfig  redisConfig, StreamTableEnvironment tableEnv){
        // 设置水位线 按照事件提取的方式 把窗口时间里的数据作为新的流对象
        DataStream<SourceEntity> watermark = sources.assignTimestampsAndWatermarks(new AssignerWithPeriodicWatermarkSeconds(1));
        String tableName = appDbindex.getAppId() + "_source_uv_sencond";
        tableEnv.registerDataStream(tableName, watermark, "r2,pvi,ip,url,random.rowtime");
        StringBuffer sql = new StringBuffer("");

        sql.append("SELECT utc2local(TUMBLE_END(random, INTERVAL '5' SECOND)) as ptime, pvi ");
        sql.append("FROM ").append(tableName);
        sql.append(" GROUP BY TUMBLE(random, INTERVAL '5' SECOND), pvi ");
        Table query = tableEnv.sqlQuery(sql.toString());
        DataStream<Tuple2<Timestamp, String>> strea = tableEnv.toAppendStream(query, Types.TUPLE(Types.SQL_TIMESTAMP, Types.STRING));
        strea.addSink(new RedisSink<Tuple2<Timestamp, String>>(redisConfig, new UvRedisSink()));
        strea.map(o -> "【5S】" +o.toString()).print();
    }

    private static class UvRedisSink implements RedisMapper<Tuple2<Timestamp, String>> {

        private static final long serialVersionUID = -5585061738635304329L;

        @Override
        public RedisCommandDescription getCommandDescription() {
            /** LPUSH 最新的数据在上面 */
            return new RedisCommandDescription(RedisCommand.PFADD, null);
        }

        @Override
        public String getKeyFromData(Tuple2<Timestamp, String> info) {
            return format.format(info.f0) + "UV";
        }

        @Override
        public String getValueFromData(Tuple2<Timestamp, String> info) {
            // 实时的计算
            return info.f1;
        }
    }


}
