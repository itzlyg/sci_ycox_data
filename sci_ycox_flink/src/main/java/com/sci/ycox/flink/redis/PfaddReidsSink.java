package com.sci.ycox.flink.redis;

import com.sci.ycox.flink.bean.SinkResult;
import com.sci.ycox.flink.util.RedisSinkKey;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.connectors.redis.RedisSink;
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisClusterConfig;
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisConfigBase;
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisPoolConfig;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommand;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommandDescription;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisMapper;

/**
 * 根据类型写redis到pfadd
 * @Description
 * @Copyright Copyright (c) 2019
 * @Company: 
 * @author Xyb
 * @Date 2019年7月11日 下午1:12:49 
 *
 */
public class                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              PfaddReidsSink  {
	
	/**
	 * 落地数据
	 * @Description
	 * @Date 2019年7月11日 下午1:15:09
	 * @param redisConfig
	 * @param type
	 * @return
	 */
	public static SinkFunction<SinkResult> sink (FlinkJedisConfigBase redisConfig, String type){
        SinkFunction<SinkResult> function = null;
		if (redisConfig instanceof FlinkJedisClusterConfig) {
            FlinkJedisClusterConfig clusterConfig = (FlinkJedisClusterConfig) redisConfig;
            function = new RedisPfSink<SinkResult>(clusterConfig, new PfRedisSink(type));
		} else if(redisConfig instanceof FlinkJedisPoolConfig){
            function = new RedisSink<SinkResult>((FlinkJedisPoolConfig) redisConfig, new PfRedisSink(type));
        } else {
			throw new IllegalArgumentException("Jedis configuration not found");
		}
		return function;
	}

	
	private static class PfRedisSink implements RedisMapper<SinkResult> {

		private static final long serialVersionUID = -5585061738635304329L;
		private String type;
		
		public PfRedisSink(String type){
			this.type = type;
		}

		@Override
		public RedisCommandDescription getCommandDescription() {
			return new RedisCommandDescription(RedisCommand.PFADD, null);
		}

		@Override
		public String getKeyFromData(SinkResult t) {
			String key = null;
			if (RedisSinkKey.VV.equals(type)) {
				key = RedisSinkKey.vvs(t);
			} else if (RedisSinkKey.UV.equals(type)) {
				key = RedisSinkKey.uvs(t);
			} else if (RedisSinkKey.IP.equals(type)) {
				key = RedisSinkKey.ips(t);
			} else {
				key = RedisSinkKey.pvs(t);
			}
			return key;
		}

		@Override
		public String getValueFromData(SinkResult t) {
			return t.getContext();
		}
	}


}
