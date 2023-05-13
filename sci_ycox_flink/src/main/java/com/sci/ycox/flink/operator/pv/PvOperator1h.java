package com.sci.ycox.flink.operator.pv;

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
 * 1小时的窗口计算 pv
 * @Description
 * @Copyright Copyright (c) 2019
 * @Company: 
 * @author Xyb
 * @Date 2019年7月5日 下午5:44:22
 *
 */
public class PvOperator1h {


	/**
	 * @Description 分区读取数据写数据到对应的redis 中
	 * @Date 2019年7月5日 下午7:45:05
	 * @param sources 数据流
	 * @param redisConfig redis
	 * @param tableEnv table环境
	 */
	public static void execute(DataStream<SourceEntity> sources, FlinkJedisConfigBase redisConfig,
                               StreamTableEnvironment tableEnv) {
		// 设置水位线 按照事件提取的方式 把窗口时间里的数据作为新的流对象
		DataStream<SourceEntity> watermark = sources.assignTimestampsAndWatermarks(new AssignerWithPeriodicWatermarkSeconds(60));
		String tableName = "data_source_pv_hour";
		tableEnv.registerDataStream(tableName, watermark, "r2,pvi,url, random.rowtime");
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT ptime, count(url) num, r2 appid, '' context FROM (  ");
        sql.append("SELECT utc2local(TUMBLE_START(random, INTERVAL '1' HOUR)) as ptime, r2, url, ");
        sql.append("min(pvi) pvi FROM ").append(tableName);
        sql.append(" GROUP BY TUMBLE(random, INTERVAL '1' HOUR), r2, pvi, url ) GROUP BY ptime, r2 ");
		Table query = tableEnv.sqlQuery(sql.toString());
        RemoveReduceProcess rrp = new RemoveReduceProcess();
        DataStream<Tuple2<Boolean, SinkResult>> stream = tableEnv.toRetractStream(query, SinkResult.class);
        SingleOutputStreamOperator<SinkResult> soso = rrp.excute(stream);
        soso.addSink(HourRedisSink.sink(redisConfig, RedisSinkKey.PV));
	}

}
