package com.sci.ycox.flink.operator.vv;

import com.sci.ycox.flink.bean.SinkResult;
import com.sci.ycox.flink.bean.SourceEntity;
import com.sci.ycox.flink.functions.AssignerWithPeriodicWatermarkSeconds;
import com.sci.ycox.flink.redis.PfaddReidsSink;
import com.sci.ycox.flink.util.RedisSinkKey;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisConfigBase;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.java.StreamTableEnvironment;

/**
 *  5秒的窗口计算 VV
 *  参数si修改一次记为 1次vv
 * @Description
 * @Copyright Copyright (c) 2019
 * @Company: 
 * @author Xyb
 * @Date 2019年7月5日 下午4:46:12
 *
 */
public class VvOperator5s {

	/**
	 * 分区读取数据写数据到对应的redis 中
	 * @Description
	 * @Date 2019年7月5日 下下午4:46:12
	 * @param sources 数据流
	 * @param redisConfig redis
	 * @param tableEnv table环境
	 */
	public static void execute(DataStream<SourceEntity> sources, FlinkJedisConfigBase redisConfig,
                               StreamTableEnvironment tableEnv) {
		DataStream<SourceEntity> watermark = sources.assignTimestampsAndWatermarks(new AssignerWithPeriodicWatermarkSeconds(1));
		String tableName = "data_source_vv_sencond";
        tableEnv.registerDataStream(tableName, watermark, "r2,pvi,si,random.rowtime");
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT utc2local(TUMBLE_END(random, INTERVAL '5' SECOND)) as ptime, r2 appid , concat(pvi, si) context, count(pvi) num ");
		sql.append("FROM ").append(tableName);
		sql.append(" GROUP BY TUMBLE(random, INTERVAL '5' SECOND), r2, pvi,si ");
		Table query = tableEnv.sqlQuery(sql.toString());
        DataStream<SinkResult> stream = tableEnv.toAppendStream(query, SinkResult.class);
        stream.addSink(PfaddReidsSink.sink(redisConfig, RedisSinkKey.VV));
        stream.map(o->"VV[5S]" + o.toString()).print();
	}

}
