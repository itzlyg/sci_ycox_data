package com.sci.ycox.flink.redis;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisClusterConfig;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommand;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommandDescription;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;

/**
 *  Redis 集羣PF指令落地
 * @Description
 * @Copyright Copyright (c) 2019
 * @Company: 
 * @author Xyb
 * @Date 2019年7月11日 下午12:57:03 
 * @param <IN>
 *
 */
public class RedisPfSink<IN> extends RichSinkFunction<IN> {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(RedisPfSink.class);
    private final RedisMapper<IN> redisSinkMapper;
    private final RedisCommand redisCommand;
    private final FlinkJedisClusterConfig flinkJedisConfigBase;
    private RedisCommandsPfContainer redisCommandsContainer;

    public RedisPfSink(FlinkJedisClusterConfig flinkJedisConfigBase, RedisMapper<IN> redisSinkMapper) {
        Objects.requireNonNull(flinkJedisConfigBase, "Redis connection pool config should not be null");
        Objects.requireNonNull(redisSinkMapper, "Redis Mapper can not be null");
        Objects.requireNonNull(redisSinkMapper.getCommandDescription(), "Redis Mapper data type description can not be null");
        this.flinkJedisConfigBase = flinkJedisConfigBase;
        this.redisSinkMapper = redisSinkMapper;
        RedisCommandDescription redisCommandDescription = redisSinkMapper.getCommandDescription();
        this.redisCommand = redisCommandDescription.getCommand();
    }

	@SuppressWarnings("rawtypes")
	@Override
	public void invoke(IN input, Context context) throws Exception {
		String key = this.redisSinkMapper.getKeyFromData(input);
		String value = this.redisSinkMapper.getValueFromData(input);
		if (Objects.requireNonNull(this.redisCommand) == RedisCommand.PFADD) {
			this.redisCommandsContainer.pfadd(key, value);
		} else {
			throw new IllegalArgumentException("Cannot process such data type: " + this.redisCommand);
		}

	}

	@Override
	public void open(Configuration parameters) throws Exception {
		try {
			this.redisCommandsContainer = ClusterPfadd.build(this.flinkJedisConfigBase);
			this.redisCommandsContainer.open();
		} catch (Exception var3) {
			LOG.error("Redis has not been properly initialized: ", var3);
			throw var3;
		}
	}

	@Override
	public void close() throws IOException {
		if (this.redisCommandsContainer != null) {
			this.redisCommandsContainer.close();
		}
	}
}
