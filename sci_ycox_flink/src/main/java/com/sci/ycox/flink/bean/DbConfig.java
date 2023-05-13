package com.sci.ycox.flink.bean;

/** 数据库配置
 * @Description
 * @Author xyb
 * @Date 2019-07-30 14:45:11
 * @Version 1.0.0
 */
public class DbConfig {

    /** 驱动名称 */
    private String driverClassName;
    /** 数据库连接地址 */
    private String url;
    /** 用户名 */
    private String username;
    /** 密码 */
    private String password;

    /**
     * 获取 驱动名称
     *
     * @return driverClassName 驱动名称
     */
    public String getDriverClassName() {
        return this.driverClassName;
    }

    /**
     * 设置 驱动名称
     *
     * @param driverClassName 驱动名称
     */
    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    /**
     * 获取 数据库连接地址
     *
     * @return url 数据库连接地址
     */
    public String getUrl() {
        return this.url;
    }

    /**
     * 设置 数据库连接地址
     *
     * @param url 数据库连接地址
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 获取 用户名
     *
     * @return username 用户名
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * 设置 用户名
     *
     * @param username 用户名
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 获取 密码
     *
     * @return password 密码
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * 设置 密码
     *
     * @param password 密码
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
