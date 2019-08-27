package com.sci.ycox.flink.operator.ip;

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
 * 1小时的窗口计算 IP
 * @Description
 * @Copyright Copyright (c) 2019
 * @Company: 
 * @author Xyb
 * @Date 2019年7月3日 下午5:44:22 
 *
 */
public class IpOperator1h {


	/**
	 * @Description 分区读取数据写数据到对应的redis 中
	 * @Date 2019年7月4日 下午5:45:05
	 * @param sources 数据流
	 * @param redisConfig redis
	 * @param tableEnv table环境
	 */
	public static void execute(DataStream<SourceEntity> sources, FlinkJedisConfigBase redisConfig,
                               StreamTableEnvironment tableEnv) {
		DataStream<SourceEntity> watermark = sources.assignTimestampsAndWatermarks(new AssignerWithPeriodicWatermarkSeconds(60));
		String tableName = "data_source_ip_hour";
		tableEnv.registerDataStream(tableName, watermark, "r2,remoteip,random.rowtime");
		StringBuffer sql = new StringBuffer("");
		sql.append("SELECT ptime, count(remoteip) num, r2 appid, '' context FROM (");
		sql.append("SELECT utc2local(TUMBLE_START(random, INTERVAL '1' HOUR)) as ptime, r2, ");
		sql.append("min(remoteip) as remoteip FROM ").append(tableName);
		sql.append(" GROUP BY TUMBLE(random, INTERVAL '1' HOUR), r2, remoteip ) group by ptime, r2 ");
		Table query = tableEnv.sqlQuery(sql.toString());
        RemoveReduceProcess rrp = new RemoveReduceProcess();
        DataStream<Tuple2<Boolean, SinkResult>> stream = tableEnv.toRetractStream(query, SinkResult.class);
        SingleOutputStreamOperator<SinkResult> soso = rrp.excute(stream);
        soso.addSink(HourRedisSink.sink(redisConfig, RedisSinkKey.IP));
	}

}
