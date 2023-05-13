package com.sci.ycox.flink.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: xyb
 * @Description:
 * @Date: 2023-05-13 上午 12:55
 **/
public class JsonUtil {

    private static final ObjectMapper OM = new ObjectMapper();
    private static final Logger LOG = LoggerFactory.getLogger(JsonUtil.class);
    /**
     * 转换为 JSON 字符串
     *
     * @param obj
     * @return
     */
    public static String toJson(Object obj) {
        try {
            return OM.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            LOG.error("对象转json错误，{}", e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 转换为 JavaBean
     *
     * @param json
     * @param clazz
     * @return
     */
    public static <T> T toPojo(String json, Class<T> clazz) {
        try {
            OM.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return OM.readValue(json, clazz);
        } catch (Exception e) {
            LOG.error("json转bean错误，{}", e.getMessage());
            throw new RuntimeException("json转bean错误");
        }
    }
}