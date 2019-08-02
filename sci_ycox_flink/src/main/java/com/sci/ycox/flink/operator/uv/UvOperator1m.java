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

public class UvOperator1m {


    private static SimpleDateFormat formathh = new SimpleDateFormat("yyyyMMddHHmm");


    /**
     * 分区读取数据写数据到对应的redis db中
     * @param appDbindex app_id---redisDB
     * @param tableEnv table环境
     */
    public static void excute (AppDbindex appDbindex, DataStream<SourceEntity> sources, FlinkJedisPoolConfig  redisConfig, StreamTableEnvironment tableEnv){
        // 设置水位线 按照事件提取的方式 把窗口时间里的数据作为新的流对象
        DataStream<SourceEntity> watermark = sources.assignTimestampsAndWatermarks(new AssignerWithPeriodicWatermarkSeconds(10));
        String tableName = appDbindex.getAppId() + "_source_uv_minute";
        tableEnv.registerDataStream(tableName, watermark, "r2,pvi,ip,url,random.rowtime");
        StringBuffer sql = new StringBuffer("");

        sql.append("SELECT utc2local(TUMBLE_END(random, INTERVAL '60' SECOND)) as ptime, count(pvi) as pvcount ");
        sql.append("FROM ").append(tableName);
        sql.append(" GROUP BY TUMBLE(random, INTERVAL '60' SECOND), pvi ");
        Table query = tableEnv.sqlQuery(sql.toString());
        DataStream<Tuple2<Timestamp, Long>> strea = tableEnv.toAppendStream(query, Types.TUPLE(Types.SQL_TIMESTAMP, Types.LONG));

        strea.addSink(new RedisSink<Tuple2<Timestamp, Long>>(redisConfig, new UvRedisSinkMinute()));
        strea.map(o -> "【1MIN】" + o.toString()).print();
    }

    private static class UvRedisSinkMinute implements RedisMapper<Tuple2<Timestamp, Long>> {

        private static final long serialVersionUID = -5458881752798151953L;

        @Override
        public RedisCommandDescription getCommandDescription() {
            /** LPUSH 最新的数据在上面 */
            return new RedisCommandDescription(RedisCommand.SET, null);
        }

        @Override
        public String getKeyFromData(Tuple2<Timestamp, Long> info) {
            return formathh.format(info.f0) + "UV";
        }

        @Override
        public String getValueFromData(Tuple2<Timestamp, Long> info) {
            // 按照小时计算
            return info.f1 + "";
        }

    }
}
