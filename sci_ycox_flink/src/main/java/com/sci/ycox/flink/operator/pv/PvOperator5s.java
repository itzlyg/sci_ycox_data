package com.sci.ycox.flink.operator.pv;

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
 *  5秒的窗口计算 PV
 * @Description
 * @Copyright Copyright (c) 2019
 * @Company: 
 * @author Xyb
 * @Date 2019年7月5日 下午8:01:12
 *
 */
public class PvOperator5s {

	/**
	 * 分区读取数据写数据到对应的redis 中
	 * @Description
	 * @Date 2019年7月5日 下午6:16:21
	 * @param sources 数据流
	 * @param redisConfig redis
	 * @param tableEnv table环境
	 */
	public static void execute(DataStream<SourceEntity> sources, FlinkJedisConfigBase redisConfig,
                               StreamTableEnvironment tableEnv) {
		// 设置水位线 按照事件提取的方式 把窗口时间里的数据作为新的流对象
		DataStream<SourceEntity> watermark = sources.assignTimestampsAndWatermarks(new AssignerWithPeriodicWatermarkSeconds(1));
		String tableName = "data_source_pv_sencond";
		tableEnv.registerDataStream(tableName, watermark, "r2, pvi, url, random.rowtime");
		StringBuffer sql = new StringBuffer("");
        sql.append("SELECT utc2local(TUMBLE_END(random, INTERVAL '5' SECOND)) as ptime, r2 appid , concat(pvi, url) context, count(pvi) num ");
        sql.append("FROM ").append(tableName);
        sql.append(" GROUP BY TUMBLE(random, INTERVAL '5' SECOND), r2, pvi, url ");
		Table query = tableEnv.sqlQuery(sql.toString());
		DataStream<SinkResult> stream = tableEnv.toAppendStream(query, SinkResult.class);
        stream.addSink(PfaddReidsSink.sink(redisConfig, RedisSinkKey.PV));
        stream.map(o -> "【PV 5S】" + o.toString()).print();
	}
}
