package com.sci.ycox.flink.bean;

import java.sql.Timestamp;

/**
 * 数据落地时候的数据bean
 * @Description
 * @Copyright Copyright (c) 2019
 * @Company: 
 * @author Xyb
 * @Date 2019年7月5日 下午2:34:12 
 *
 */
public class SinkResult {

    /** appid */
    private String appid;

    /** 内容 */
    private String context;
    /** 数量 */
    private long num;
    /** 时间 */
    private Timestamp ptime;


    /**
     * 获取 appid
     *
     * @return appid appid
     */
    public String getAppid() {
        return this.appid;
    }

    /**
     * 设置 appid
     *
     * @param appid appid
     */
    public void setAppid(String appid) {
        this.appid = appid;
    }

    /**
     * 获取 内容
     *
     * @return context 内容
     */
    public String getContext() {
        return this.context;
    }

    /**
     * 设置 内容
     *
     * @param context 内容
     */
    public void setContext(String context) {
        this.context = context;
    }

    /**
     * 获取 数量
     *
     * @return num 数量
     */
    public long getNum() {
        return this.num;
    }

    /**
     * 设置 数量
     *
     * @param num 数量
     */
    public void setNum(long num) {
        this.num = num;
    }

    /**
     * 获取 时间
     *
     * @return ptime 时间
     */
    public Timestamp getPtime() {
        return this.ptime;
    }

    /**
     * 设置 时间
     *
     * @param ptime 时间
     */
    public void setPtime(Timestamp ptime) {
        this.ptime = ptime;
    }


    @Override
    public String toString() {
        return "SinkResult{" +
                "appid='" + appid + '\'' +
                ", context='" + context + '\'' +
                ", num=" + num +
                ", ptime=" + ptime +
                '}';
    }
}
