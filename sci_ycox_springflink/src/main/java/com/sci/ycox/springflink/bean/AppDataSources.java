package com.sci.ycox.springflink.bean;

import java.util.Date;

/**
 * @Description
 * @Author xyb
 * @Date 2019-07-23 11:06:31
 * @Version 1.0.0
 */
public class AppDataSources {
    /**
     * appid
     */
    private String r2;

    /**
     * 用户id
     */
    private String pvi;

    /**
     * pgv_si
     */
    private String si;

    /**
     * 时间戳
     */
    private Date random;

    /**
     * 路径
     */
    private String url;

    /**
     * appid
     * @return r2 appid
     */
    public String getR2() {
        return r2;
    }

    /**
     * appid
     * @param r2 appid
     */
    public void setR2(String r2) {
        this.r2 = r2;
    }

    /**
     * 用户id
     * @return pvi 用户id
     */
    public String getPvi() {
        return pvi;
    }

    /**
     * 用户id
     * @param pvi 用户id
     */
    public void setPvi(String pvi) {
        this.pvi = pvi;
    }

    /**
     * pgv_si
     * @return si pgv_si
     */
    public String getSi() {
        return si;
    }

    /**
     * pgv_si
     * @param si pgv_si
     */
    public void setSi(String si) {
        this.si = si;
    }

    /**
     * 时间戳
     * @return random 时间戳
     */
    public Date getRandom() {
        return random;
    }

    /**
     * 时间戳
     * @param random 时间戳
     */
    public void setRandom(Date random) {
        this.random = random;
    }

    /**
     * 路径
     * @return url 路径
     */
    public String getUrl() {
        return url;
    }

    /**
     * 路径
     * @param url 路径
     */
    public void setUrl(String url) {
        this.url = url;
    }
}