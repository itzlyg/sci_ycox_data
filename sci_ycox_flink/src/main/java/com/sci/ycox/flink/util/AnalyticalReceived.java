package com.sci.ycox.flink.util;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * 
 * @Description
 * @Copyright Copyright (c) 2019
 * @author Xyb
 * @Date 2019年8月16日 下午5:09:10 
 *
 */
public class AnalyticalReceived {

	/** 临时二维码 ticket */
	private String ticket;
	
	/*** 短链接相关 **/
	/** 错误码 0成功 40013 失败 */
	private int errcode;
	
	/** 错误消息 */
	private String errmsg;
	
	/** 短链接地址 */
    @JsonProperty(value = "short_url")
	private String shortUrl;
	
	/** token类  **/
	/** accessToken **/
    @JsonProperty(value = "access_token")
	private String accessToken;
	
	/** expires_in 过期时间 **/
    @JsonProperty(value = "expires_in")
	private String expiresIn;
	/** openid */
	private String openid;
	
	/** 用户信息 */
	/** 用户昵称 */
	private String nickname;
	/** 用户头像地址 */
	private String headimgurl;
	/** 性别 */
	private int sex;
	/** 用户头像地址 */
	private String language;
	/** 用户国家 */
	private String country;
	/** 用户省份 */
	private String province;
	/** 用户城市 */
	private String city;
	/** privilege */
	private List<Object> privilege;

    /**
     * 获取 临时二维码 ticket
     *
     * @return ticket 临时二维码 ticket
     */
    public String getTicket() {
        return this.ticket;
    }

    /**
     * 设置 临时二维码 ticket
     *
     * @param ticket 临时二维码 ticket
     */
    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    /**
     * 获取 错误码 0成功 40013 失败
     *
     * @return errcode 错误码 0成功 40013 失败
     */
    public int getErrcode() {
        return this.errcode;
    }

    /**
     * 设置 错误码 0成功 40013 失败
     *
     * @param errcode 错误码 0成功 40013 失败
     */
    public void setErrcode(int errcode) {
        this.errcode = errcode;
    }

    /**
     * 获取 错误消息
     *
     * @return errmsg 错误消息
     */
    public String getErrmsg() {
        return this.errmsg;
    }

    /**
     * 设置 错误消息
     *
     * @param errmsg 错误消息
     */
    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    /**
     * 获取 短链接地址
     *
     * @return shortUrl 短链接地址
     */
    public String getShortUrl() {
        return this.shortUrl;
    }

    /**
     * 设置 短链接地址
     *
     * @param shortUrl 短链接地址
     */
    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    /**
     * 获取 accessToken
     *
     * @return accessToken accessToken
     */
    public String getAccessToken() {
        return this.accessToken;
    }

    /**
     * 设置 accessToken
     *
     * @param accessToken accessToken
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * 获取 expires_in 过期时间
     *
     * @return expiresIn expires_in 过期时间
     */
    public String getExpiresIn() {
        return this.expiresIn;
    }

    /**
     * 设置 expires_in 过期时间
     *
     * @param expiresIn expires_in 过期时间
     */
    public void setExpiresIn(String expiresIn) {
        this.expiresIn = expiresIn;
    }

    /**
     * 获取 openid
     *
     * @return openid openid
     */
    public String getOpenid() {
        return this.openid;
    }

    /**
     * 设置 openid
     *
     * @param openid openid
     */
    public void setOpenid(String openid) {
        this.openid = openid;
    }

    /**
     * 获取 用户昵称
     *
     * @return nickname 用户昵称
     */
    public String getNickname() {
        return this.nickname;
    }

    /**
     * 设置 用户昵称
     *
     * @param nickname 用户昵称
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * 获取 用户头像地址
     *
     * @return headimgurl 用户头像地址
     */
    public String getHeadimgurl() {
        return this.headimgurl;
    }

    /**
     * 设置 用户头像地址
     *
     * @param headimgurl 用户头像地址
     */
    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }

    /**
     * 获取 性别
     *
     * @return sex 性别
     */
    public int getSex() {
        return this.sex;
    }

    /**
     * 设置 性别
     *
     * @param sex 性别
     */
    public void setSex(int sex) {
        this.sex = sex;
    }

    /**
     * 获取 用户头像地址
     *
     * @return language 用户头像地址
     */
    public String getLanguage() {
        return this.language;
    }

    /**
     * 设置 用户头像地址
     *
     * @param language 用户头像地址
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * 获取 用户国家
     *
     * @return country 用户国家
     */
    public String getCountry() {
        return this.country;
    }

    /**
     * 设置 用户国家
     *
     * @param country 用户国家
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * 获取 用户省份
     *
     * @return province 用户省份
     */
    public String getProvince() {
        return this.province;
    }

    /**
     * 设置 用户省份
     *
     * @param province 用户省份
     */
    public void setProvince(String province) {
        this.province = province;
    }

    /**
     * 获取 用户城市
     *
     * @return city 用户城市
     */
    public String getCity() {
        return this.city;
    }

    /**
     * 设置 用户城市
     *
     * @param city 用户城市
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * 获取 privilege
     *
     * @return privilege privilege
     */
    public List<Object> getPrivilege() {
        return this.privilege;
    }

    /**
     * 设置 privilege
     *
     * @param privilege privilege
     */
    public void setPrivilege(List<Object> privilege) {
        this.privilege = privilege;
    }

    @Override
    public String toString() {
        return "AnalyticalReceived{" +
                "ticket='" + ticket + '\'' +
                ", errcode=" + errcode +
                ", errmsg='" + errmsg + '\'' +
                ", shortUrl='" + shortUrl + '\'' +
                ", accessToken='" + accessToken + '\'' +
                ", expiresIn='" + expiresIn + '\'' +
                ", openid='" + openid + '\'' +
                ", nickname='" + nickname + '\'' +
                ", headimgurl='" + headimgurl + '\'' +
                ", sex=" + sex +
                ", language='" + language + '\'' +
                ", country='" + country + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", privilege=" + privilege +
                '}';
    }
}
