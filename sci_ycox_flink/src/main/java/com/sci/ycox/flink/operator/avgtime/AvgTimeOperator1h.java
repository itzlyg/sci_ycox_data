package com.sci.ycox.flink.operator.avgtime;

import com.sci.ycox.flink.bean.SinkResult;
import com.sci.ycox.flink.bean.SourceEntity;
import com.sci.ycox.flink.functions.AssignerWithPeriodicWatermarkSeconds;
import com.sci.ycox.flink.functions.RemoveReduceProcess;
import com.sci.ycox.flink.redis.HourRedisSink;
import com.sci.ycox.flink.util.RedisSinkKey;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisConfigBase;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.java.StreamTableEnvironment;

/**
 *
 * @Description 1 小时窗口平均时间长统计
 * @Copyright Copyright (c) 2019
 * @Company: 
 * @author Xyb
 * @Date 2019年7月15日 下午5:21:43 
 *
 */
public class AvgTimeOperator1h {

    public static void execute(DataStream<SourceEntity> sources, FlinkJedisConfigBase redisConfig,
                               StreamTableEnvironment tableEnv) {
        DataStream<SourceEntity> watermark = sources.assignTimestampsAndWatermarks(new AssignerWithPeriodicWatermarkSeconds(60));
        String tableName = "data_source_avgt_hour";
        tableEnv.registerDataStream(tableName, watermark, "r2,pvi, si, url, random.rowtime");
        StringBuffer sql = new StringBuffer("");
        sql.append("SELECT ptime, CAST(sum(t) AS BIGINT) num, r2 appid, '' context FROM (  ");
        sql.append("SELECT utc2local(TUMBLE_START(random, INTERVAL '1' HOUR)) as ptime, r2, ");
        sql.append("TIMESTAMPDIFF(SECOND, min(random), max(random)) t FROM ").append(tableName);
        sql.append(" GROUP BY TUMBLE(random, INTERVAL '1' HOUR), r2, pvi, si, url) GROUP BY ptime, r2 ");
        Table query = tableEnv.sqlQuery(sql.toString());
        RemoveReduceProcess rrp = new RemoveReduceProcess();
        DataStream<Tuple2<Boolean, SinkResult>> stream = tableEnv.toRetractStream(query, SinkResult.class);
        SingleOutputStreamOperator<SinkResult> soso = rrp.excute(stream);
        soso.addSink(HourRedisSink.sink(redisConfig, RedisSinkKey.AVGT));
        soso.map(s -> s.toString()).print();
    }

}
