package com.sci.ycox.flink.bean;

import java.sql.Timestamp;

/**
 * 解析kafka数据对应的bean
 * @Description
 * @Copyright Copyright (c) 2019
 * @Company: 
 * @author Xyb
 * @Date 2019年7月3日 下午5:47:47 
 *
 */
public class SourceEntity {

	/** 渠道id */
	private String r2;

	/** 用户id */
	private String pvi;
	
	/** pgv_si */
	private String si;

	/** ip */
	private String remoteip;

	/** dm */
	private String dm;

	/** 是否首次访问 */
	private String ty;

	/** url */
	private String url;

    /** scl */
    private String scl;

	/** 系统语言 */
	private String lg;

	/** 时区 */
	private String tz;

	/** 性别 */
	private String sex;
	
	/** 版本 */
	private String ext;

    /** 分辨率 */
    private String scr;

    /** 城市 */
    private String city;

    /** 省份 */
    private String province;

    /** 国家 */
    private String country;

    /** 年龄 */
    private int age;
	
	/** 时间戳 */
	private Timestamp random;


    /**
     * 获取 渠道id
     *
     * @return r2 渠道id
     */
    public String getR2() {
        return this.r2;
    }

    /**
     * 设置 渠道id
     *
     * @param r2 渠道id
     */
    public void setR2(String r2) {
        this.r2 = r2;
    }

    /**
     * 获取 用户id
     *
     * @return pvi 用户id
     */
    public String getPvi() {
        return this.pvi;
    }

    /**
     * 设置 用户id
     *
     * @param pvi 用户id
     */
    public void setPvi(String pvi) {
        this.pvi = pvi;
    }

    /**
     * 获取 pgv_si
     *
     * @return si pgv_si
     */
    public String getSi() {
        return this.si;
    }

    /**
     * 设置 pgv_si
     *
     * @param si pgv_si
     */
    public void setSi(String si) {
        this.si = si;
    }

    /**
     * 获取 ip
     *
     * @return remoteip ip
     */
    public String getRemoteip() {
        return this.remoteip;
    }

    /**
     * 设置 ip
     *
     * @param remoteip ip
     */
    public void setRemoteip(String remoteip) {
        this.remoteip = remoteip;
    }

    /**
     * 获取 dm
     *
     * @return dm dm
     */
    public String getDm() {
        return this.dm;
    }

    /**
     * 设置 dm
     *
     * @param dm dm
     */
    public void setDm(String dm) {
        this.dm = dm;
    }

    /**
     * 获取 是否首次访问
     *
     * @return ty 是否首次访问
     */
    public String getTy() {
        return this.ty;
    }

    /**
     * 设置 是否首次访问
     *
     * @param ty 是否首次访问
     */
    public void setTy(String ty) {
        this.ty = ty;
    }

    /**
     * 获取 url
     *
     * @return url url
     */
    public String getUrl() {
        return this.url;
    }

    /**
     * 设置 url
     *
     * @param url url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 获取 scl
     *
     * @return scl scl
     */
    public String getScl() {
        return this.scl;
    }

    /**
     * 设置 scl
     *
     * @param scl scl
     */
    public void setScl(String scl) {
        this.scl = scl;
    }

    /**
     * 获取 系统语言
     *
     * @return lg 系统语言
     */
    public String getLg() {
        return this.lg;
    }

    /**
     * 设置 系统语言
     *
     * @param lg 系统语言
     */
    public void setLg(String lg) {
        this.lg = lg;
    }

    /**
     * 获取 时区
     *
     * @return tz 时区
     */
    public String getTz() {
        return this.tz;
    }

    /**
     * 设置 时区
     *
     * @param tz 时区
     */
    public void setTz(String tz) {
        this.tz = tz;
    }

    /**
     * 获取 性别
     *
     * @return sex 性别
     */
    public String getSex() {
        return this.sex;
    }

    /**
     * 设置 性别
     *
     * @param sex 性别
     */
    public void setSex(String sex) {
        this.sex = sex;
    }

    /**
     * 获取 版本
     *
     * @return ext 版本
     */
    public String getExt() {
        return this.ext;
    }

    /**
     * 设置 版本
     *
     * @param ext 版本
     */
    public void setExt(String ext) {
        this.ext = ext;
    }

    /**
     * 获取 分辨率
     *
     * @return scr 分辨率
     */
    public String getScr() {
        return this.scr;
    }

    /**
     * 设置 分辨率
     *
     * @param scr 分辨率
     */
    public void setScr(String scr) {
        this.scr = scr;
    }

    /**
     * 获取 城市
     *
     * @return city 城市
     */
    public String getCity() {
        return this.city;
    }

    /**
     * 设置 城市
     *
     * @param city 城市
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * 获取 省份
     *
     * @return province 省份
     */
    public String getProvince() {
        return this.province;
    }

    /**
     * 设置 省份
     *
     * @param province 省份
     */
    public void setProvince(String province) {
        this.province = province;
    }

    /**
     * 获取 国家
     *
     * @return country 国家
     */
    public String getCountry() {
        return this.country;
    }

    /**
     * 设置 国家
     *
     * @param country 国家
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * 获取 年龄
     *
     * @return age 年龄
     */
    public int getAge() {
        return this.age;
    }

    /**
     * 设置 年龄
     *
     * @param age 年龄
     */
    public void setAge(int age) {
        this.age = age;
    }

    /**
     * 获取 时间戳
     *
     * @return random 时间戳
     */
    public Timestamp getRandom() {
        return this.random;
    }

    /**
     * 设置 时间戳
     *
     * @param random 时间戳
     */
    public void setRandom(Timestamp random) {
        this.random = random;
    }
}
