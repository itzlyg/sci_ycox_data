package com.sci.ycox.flink.redis;

import java.io.IOException;

/**
 * redis 集羣
 * @Description
 * @Copyright Copyright (c) 2019
 * @Company: 
 * @author Xyb
 * @Date 2019年7月11日 下午12:58:01 
 *
 */
public interface RedisCommandsPfContainer {
	
	/**
	 * 打开资源
	 * @Description
	 * @Date 2019年7月17日 上午10:06:10
	 * @throws Exception
	 */
    void open() throws Exception;

    /**
     * PFADD指令
     * @Description
     * @Date 2019年7月17日 上午10:06:42
     * @param key key
     * @param value value
     */
    void pfadd(String key, String value);

    /**
     * 关闭资源
     * @Description
     * @Date 2019年7月17日 上午10:07:18
     * @throws IOException
     */
    void close() throws IOException;
}
