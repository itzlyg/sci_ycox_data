package com.sci.ycox.flink.bean;

import java.sql.Timestamp;

public class ResultInfo {
    private String appId;
    private String pvi;
    private String name;
    private Timestamp ptime;
    private Long pvcount;

    public ResultInfo() {
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getPvi() {
        return pvi;
    }

    public void setPvi(String pvi) {
        this.pvi = pvi;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Timestamp getPtime() {
        return ptime;
    }

    public void setPtime(Timestamp ptime) {
        this.ptime = ptime;
    }

    public Long getPvcount() {
        return pvcount;
    }

    public void setPvcount(Long pvcount) {
        this.pvcount = pvcount;
    }

    @Override
    public String toString() {
        return "ResultInfo{" +
                "appId='" + appId + '\'' +
                ", pvi='" + pvi + '\'' +
                ", name='" + name + '\'' +
                ", ptime=" + ptime +
                ", pvcount=" + pvcount +
                '}';
    }
}
