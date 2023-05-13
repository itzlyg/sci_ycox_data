package com.sci.ycox.flink.util;


import com.sci.ycox.flink.bean.SinkResult;

/**
 * redis sink 的时候key值 
 * @Description
 * @Copyright Copyright (c) 2019
 * @Company: 
 * @author Xyb
 * @Date 2019年7月3日 下午5:50:40 
 *
 */
public class RedisSinkKey {

    public static String UV = "UV";

    public static String IP = "IP";

    public static String VV = "VV";

    public static String PV = "PV";

    /** 平均时长 */
    public static String AVGT = "AVGT";

    private static String H = "-H";

    /**
     * 	平均时间长 1H窗口的key
     * @Description
     * @Date 2019年7月15日 下午17:11:55
     * @param t 结果集
     * @return key
     */
    public static String avgth(SinkResult t){
        return format(t, AVGT + H);
    }

    /**
     * pv 5S窗口的key
     * @Description
     * @Date 2019年7月5日 下午6:19:38
     * @param t 结果集
     * @return key
     */
    public static String pvs(SinkResult t){
        return format(t, PV);
    }

    /**
     * pv 1H窗口的key
     * @Description
     * @Date 2019年7月5日 下午6:19:38
     * @param t 结果集
     * @return key
     */
    public static String pvh(SinkResult t){
        return format(t, PV + H);
    }

    /**
     * vv 5S窗口的key
     * @Description
     * @Date 2019年7月5日 下午6:19:38
     * @param t 结果集
     * @return key
     */
    public static String vvs(SinkResult t){
        return format(t, VV);
    }

    /**
     * vv 1H窗口的key
     * @Description
     * @Date 2019年7月5日 下午6:19:38
     * @param t 结果集
     * @return key
     */
    public static String vvh(SinkResult t){
        return format(t, VV + H);
    }


    /**
     * UV 按照秒窗口计算
     * @Description
     * @Date 2019年7月3日 下午5:56:40
     * @param t 结果集
     * @return key
     */
    public static String uvs(SinkResult t) {
        return format(t, UV);
    }

    /**
     * UV 按照小时窗口计算
     * @Description
     * @Date 2019年7月3日 下午5:56:40
     * @param t 结果集
     * @return key
     */
    public static String uvh(SinkResult t) {
        return format(t, UV + H);
    }

    /**
     * IP 按照秒窗口计算
     * @Description
     * @Date 2019年7月3日 下午5:56:40
     * @param t 结果集
     * @return key
     */
    public static String ips(SinkResult t) {
        return format(t, IP);
    }

    /**
     * IP 按照小时窗口计算
     * @Description
     * @Date 2019年7月3日 下午5:56:40
     * @param t 结果集
     * @return key
     */
    public static String iph(SinkResult t) {
        return format(t, IP + H);
    }

    /**
     * @Description 小时的key
     * @Date 2019-08-01 09:47:54
     * @param t
     * @param type
     * @return java.lang.String
     * @throws
     */
    public static String hkey (SinkResult t, String type){
        return format(t, type + H);
    }

    /**
     * 格式化key
     * @Description
     * @Date 2019年7月3日 下午5:57:27
     * @param sr 对象
     * @param type
     * @return key
     */
    private static String format(SinkResult sr, String type) {
        return String.format("%s:%s:%s", sr.getAppid(), DateUtil.formatDateTime(sr.getPtime(), DateUtil.YYYY_MM_DD), type);
    }
}
