package com.sci.ycox.flink.redis;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisClusterConfig;
import redis.clients.jedis.JedisCluster;

import java.util.Objects;

/**
 * Redis 集群写 pf指令初始化
 * @Description
 * @Copyright Copyright (c) 2019
 * @Company: 
 * @author Xyb
 * @Date 2019年7月11日 下午12:55:52 
 *
 */
public class ClusterPfadd {

    public static RedisClusterPfContainer build(FlinkJedisClusterConfig jedisClusterConfig) {
        Objects.requireNonNull(jedisClusterConfig, "Redis cluster config should not be Null");
        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
        genericObjectPoolConfig.setMaxIdle(jedisClusterConfig.getMaxIdle());
        genericObjectPoolConfig.setMaxTotal(jedisClusterConfig.getMaxTotal());
        genericObjectPoolConfig.setMinIdle(jedisClusterConfig.getMinIdle());
        JedisCluster jedisCluster = new JedisCluster(jedisClusterConfig.getNodes(), jedisClusterConfig.getConnectionTimeout(), jedisClusterConfig.getMaxRedirections(), genericObjectPoolConfig);
        return new RedisClusterPfContainer(jedisCluster);

    }

}
