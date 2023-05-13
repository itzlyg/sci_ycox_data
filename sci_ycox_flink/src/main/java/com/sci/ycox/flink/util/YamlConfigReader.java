package com.sci.ycox.flink.util;

import com.sci.ycox.flink.bean.DbConfig;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisClusterConfig;
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisPoolConfig;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 	读取yaml的配置信息 
 * @Description
 * @Copyright Copyright (c) 2019
 * @author Xyb
 * @Date 2019年7月12日 下午1:22:32 
 *
 */
@SuppressWarnings("unchecked")
public class YamlConfigReader {

	/** application配置对象 */
    private static Map<String, Object> configs;

    /** redis 集群配置 */
    private static FlinkJedisClusterConfig clusterConfig;

    /** redis单机 Builder  */
    private static FlinkJedisPoolConfig.Builder poolBuild;

    /** redis 单机配置 */
    private static FlinkJedisPoolConfig poolConfig;

    /** kafka配置 */
    private static Properties kafka;

    /** 数据库配置 **/
    private static DbConfig dbConfig;

    /** kafka配置前缀 */
    private static final String KAFKA_PREFIX = "flink-kafka.";

    /** redis配置前缀 */
    private static final String REDIS_PREFIX = "redis.";

    /** 数据库连接配置 */
    public static final String DB_PREFIX = "spring.datasource.";

    /** 分割符号 */
    private final static String SEPARATOR = ".";

    /** 通配中间有'-' **/
    public static final Pattern P = Pattern.compile("-(\\w)");

    static {
        if (configs == null) {
            configs = readYaml("/application.yml");
        }
    }
    
    /**
     * 	将yaml的配置信息转为map对象
     * @Description
     * @Date 2019年7月12日 下午1:07:07
     * @param name 文件名称
     * @return 集合
     */
	public static Map<String, Object> readYaml(String name) {
		String prefix = "/";
		if (!StringUtils.startsWith(name, prefix)) {
			name = prefix + name;
		}
		Yaml yaml = new Yaml();
		InputStream is = YamlConfigReader.class.getResourceAsStream(name);
		Map<String, Object> map = yaml.loadAs(is, Map.class);
		Map<String, Object> result = new HashMap<>(16);
		analysis("", result, map);
		return result;
	}

    /**
     * 读取配置信息
     * @Description
     * @Date 2019年7月12日 下午4:37:19
     * @return 读取配置信息
     */
    public static Map<String, Object> getConfigs() {
        return configs;
    }

    /**
	 * 读取redis集群的配置信息
	 * @Description
	 * @Date 2019年7月12日 下午1:07:40
	 * @return redis集群的配置信息
	 */
	public static FlinkJedisClusterConfig getClusterConfig() {
		if (clusterConfig == null) {
			clusterConfig = redisCluster();
		}
		return clusterConfig;
	}

	/**
	 * 	读取单机redis的build信息
	 * @Description
	 * @Date 2019年7月12日 下午1:07:59
	 * @return 单机redis的build信息
	 */ 
	public static FlinkJedisPoolConfig.Builder getPoolBuild() {
		if (poolBuild == null) {
			poolBuild = redisPoolConfigBuilder();
		}
		return poolBuild;
	}

	/**
	 * 	读取单机的redis配置信息
	 * @Description
	 * @Date 2019年7月12日 下午1:08:24
	 * @return 单机的redis配置信息
	 */
	public static FlinkJedisPoolConfig getPoolConfig() {
		if (poolConfig == null) {
			poolConfig = getPoolBuild().build();
		}
		return poolConfig;
	}

	/**
	 *  读取kafka的配置信息
	 * @Description
	 * @Date 2019年7月12日 下午1:08:51
	 * @return kafka的配置信息
	 */
	public static Properties getKafka() {
		if (kafka == null) {
			kafka = kafka();
		}
		return kafka;
	}

	/**
	 * 将yaml配置 解析成配置
	 * @Description
	 * @Date 2019年7月12日 下午1:09:58
	 * @return 单机redis build
	 */
    private static FlinkJedisPoolConfig.Builder redisPoolConfigBuilder (){
        FlinkJedisPoolConfig.Builder builder = new FlinkJedisPoolConfig.Builder();
        String host = MapUtils.getString(configs, REDIS_PREFIX + "host");
        if (StringUtils.isBlank(host)) {
            throw new IllegalArgumentException("Jedis host not found");
        }
        builder.setHost(host);
        setValue(builder, "single" + SEPARATOR);
        return builder;
    }


    /**
     * 	从yaml 读取redis集群的配置信息
     * @Description
     * @Date 2019年7月12日 下午1:11:58
     * @return redis集群配置
     */
    private static FlinkJedisClusterConfig redisCluster (){
        FlinkJedisClusterConfig.Builder builder = new FlinkJedisClusterConfig.Builder();
        String clusterNodes = MapUtils.getString(configs,  REDIS_PREFIX + "clusterNodes");
        if (StringUtils.isBlank(clusterNodes)) {
            throw new IllegalArgumentException("Jedis clusterNodes not found");
        }
        String[] ipPorts = StringUtils.split(clusterNodes, ",");
        Set<InetSocketAddress> sets = new HashSet<>();
        String[] arrs = null;
        for (String ipPort : ipPorts) {
            arrs = StringUtils.split(ipPort, ":");
            sets.add(new InetSocketAddress(arrs[0], Integer.parseInt(arrs[1])));
        }
        builder.setNodes(sets);
        setValue(builder, "cluster" + SEPARATOR);
        return  builder.build();
    }


    /**
     * kafka 连接配置信息
     * @Description
     * @Date 2019年7月12日 下午1:17:05
     * @return kafka连接配置
     */
    private static Properties kafka (){
        Properties p = new Properties();
        String key ;
        for (Map.Entry<String, Object> e : configs.entrySet()) {
            key = e.getKey();
            if (StringUtils.startsWith(e.getKey(), KAFKA_PREFIX)){
                p.put(StringUtils.replace(key, KAFKA_PREFIX, ""), e.getValue());
            }
        }
        return  p;
    }

    /**
     * 	递归解析yaml
     * @Description
     * @Date 2019年7月12日 下午1:22:15
     * @param root
     * @param result
     * @param map
     */
    private static void analysis (String root, Map<String, Object> result, Map<String, Object> map){
        Object v;
        String key = "";
        if (StringUtils.isNotBlank(root)){
            key = root + SEPARATOR;
        }
        for (Map.Entry<String, Object> e : map.entrySet()) {
            v = e.getValue();
            if (v instanceof Map) {
                analysis(key + e.getKey(), result, (Map<String, Object>)v);
            } else {
                result.put(key + e.getKey(), e.getValue());
            }
        }
    }

    /**
     * @Description 反射赋值
     * @Date 2019-07-18 18:42:53
     * @param o 变量
     * @param middleKey 中间属性
     * @return void
     * @throws
     */
    private static void setValue (Object o, String middleKey) {
        Class<?> clazz = o.getClass();
        String n ;
        Field f ;
        Object v;
        String name;
        String common = "common";
        try {
            for (Map.Entry<String, Object> e: configs.entrySet()) {
                v = e.getValue();
                name = e.getKey();
                if (v == null || !name.startsWith(REDIS_PREFIX)) {
                    continue;
                }
                // redis 配置
                if (StringUtils.containsAny(name, common, middleKey)) {
                    n = lineToHump(StringUtils.substringAfterLast(name, SEPARATOR));
                    f = clazz.getDeclaredField(n);
                    f.setAccessible(true);
                    if (f.getType() == int.class) {
                        f.setInt(o, (int)v);
                    } else {
                        f.set(o, String.valueOf(v));
                    }
                }
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    /** 数据库连接
     * @Description
     * @Date 2019-07-30 14:40:52
     * @param
     * @return Map<String, String>
     * @throws
     */
    public static DbConfig getDbConfig() {
        if (dbConfig == null) {
            dbConfig = new DbConfig();
            Class<?> clazz = dbConfig.getClass();
            String n ;
            Field f ;
            try {
                for (Map.Entry<String, Object> e : configs.entrySet()) {
                    if (e.getKey().startsWith(DB_PREFIX)) {
                        n = lineToHump(StringUtils.substringAfterLast(e.getKey(), SEPARATOR));
                        f = clazz.getDeclaredField(n);
                        f.setAccessible(true);
                        f.set(dbConfig, String.valueOf(e.getValue()));
                    }
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return dbConfig;
    }


    /** 转驼峰
     * @Description
     * @Date 2019-07-18 18:43:33
     * @param name
     * @return java.lang.String
     * @throws
     */
    private static String lineToHump (String name){
        Matcher matcher = P.matcher(name);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
