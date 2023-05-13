package com.sci.ycox.flink.operator.vv;

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
 *  1小时的窗口计算 VV
 * @Description
 * @Copyright Copyright (c) 2019
 * @Company: 
 * @author Xyb
 * @Date 2019年7月5日 下午4:46:12
 *
 */
public class VvOperator1h {

	/**
	 * @Description 分区读取数据写数据到对应的redis 中
	 * @Date 2019年7月5日 下下午4:46:12
	 * @param sources 数据流
	 * @param redisConfig redis
	 * @param tableEnv table环境
	 */
	public static void execute(DataStream<SourceEntity> sources, FlinkJedisConfigBase redisConfig,
                               StreamTableEnvironment tableEnv) {
		DataStream<SourceEntity> watermark = sources.assignTimestampsAndWatermarks(new AssignerWithPeriodicWatermarkSeconds(60));
		String tableName = "data_source_vv_hour";
        tableEnv.registerDataStream(tableName, watermark, "r2,pvi,si,random.rowtime");
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT ptime, appid , appid context, sum(csi) num ");
		sql.append("FROM (select ptime , appid, count(si) csi from (");
		sql.append("SELECT utc2local(TUMBLE_START(random, INTERVAL '1' HOUR)) as ptime, r2 appid , pvi, si ");
		sql.append("FROM ").append(tableName);
		sql.append(" GROUP BY TUMBLE(random, INTERVAL '1' HOUR), r2,  pvi,si ");
        sql.append(") y GROUP BY ptime, appid , pvi ");
        sql.append(") z GROUP BY ptime, appid ");
		Table query = tableEnv.sqlQuery(sql.toString());
        RemoveReduceProcess rrp = new RemoveReduceProcess();
        DataStream<Tuple2<Boolean, SinkResult>> stream = tableEnv.toRetractStream(query, SinkResult.class);
        SingleOutputStreamOperator<SinkResult> soso = rrp.excute(stream);
        soso.addSink(HourRedisSink.sink(redisConfig, RedisSinkKey.VV));
        soso.map(SinkResult::toString).print();
    }

}
