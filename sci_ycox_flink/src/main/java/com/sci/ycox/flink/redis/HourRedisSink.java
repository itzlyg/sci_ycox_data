package com.sci.ycox.flink.redis;

import com.sci.ycox.flink.bean.SinkResult;
import com.sci.ycox.flink.util.DateUtil;
import com.sci.ycox.flink.util.RedisSinkKey;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.connectors.redis.RedisSink;
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisConfigBase;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommand;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommandDescription;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisMapper;

/**
 * 写入小时数据到redis
 * @Description
 * @Copyright Copyright (c) 2019
 * @Company: 
 * @author Xyb
 * @Date 2019年7月15日 下午6:12:49
 *
 */
public class HourRedisSink {
	
	/**
	 * 落地数据
	 * @Description
	 * @Date 2019年7月15日 下午5:55:09
	 * @param redisConfig
	 * @param type
	 * @return
	 */
	public static SinkFunction<SinkResult> sink (FlinkJedisConfigBase redisConfig, String type){
        return new RedisSink<SinkResult>(redisConfig, new HourSink(type));
	}

	
	private static class HourSink implements RedisMapper<SinkResult> {


		/**
		 * @Filelds serialVersionUID :
		 */
		private static final long serialVersionUID = 5422021192902833741L;

		private final String type;
		
		public HourSink(String type){
			this.type = type;
		}

		@Override
		public RedisCommandDescription getCommandDescription() {
            /** LPUSH 最新的数据在上面 */
            return new RedisCommandDescription(RedisCommand.SET, null);
		}

		@Override
		public String getKeyFromData(SinkResult t) {
			return RedisSinkKey.hkey(t, type);
		}

		@Override
		public String getValueFromData(SinkResult t) {
            return DateUtil.formatDateTime(t.getPtime(), DateUtil.HH) + t.getNum();
		}
	}
}
