package com.sci.ycox.flink.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisCluster;

import java.io.Closeable;
import java.io.IOException;
import java.util.Objects;

/**
 * Redis 集羣 PF指令實現
 * @Description
 * @Copyright Copyright (c) 2019
 * @Company: 
 * @author Xyb
 * @Date 2019年7月11日 下午12:57:36 
 *
 */
public class RedisClusterPfContainer implements RedisCommandsPfContainer, Closeable {
    private static final Logger LOG = LoggerFactory.getLogger(RedisClusterPfContainer.class);
    private transient JedisCluster jedisCluster;

    public RedisClusterPfContainer(JedisCluster jedisCluster) {
        Objects.requireNonNull(jedisCluster, "Jedis cluster can not be null");
        this.jedisCluster = jedisCluster;
    }

    @Override
    public void open() throws Exception {
        this.jedisCluster.echo("Test");
    }

    @Override
    public void pfadd(String key, String element) {
        try {
            this.jedisCluster.pfadd(key, element);
        } catch (Exception e) {
            if (LOG.isErrorEnabled()) {
                LOG.error("Cannot send Redis message with command PFADD to key {} error message {}", key, e.getMessage());
            }

            throw e;
        }
    }

    @Override
    public void close() throws IOException {
        this.jedisCluster.close();
    }
}
