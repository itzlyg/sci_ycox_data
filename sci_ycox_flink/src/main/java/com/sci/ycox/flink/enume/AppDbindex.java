package com.sci.ycox.flink.enume;

public enum AppDbindex {
    APP_A0001("APP_A0001", 1),
    APP_A0002("APP_A0002", 2),
    APP_A0003("APP_A0003", 3),
    APP_A0004("APP_A0004", 4),
    APP_B0001("APP_B0001", 5),
    APP_B0011("APP_B0011", 6),
    APP_A0021("APP_A0021", 7),
    APP_A0041("APP_A0041", 8)
    ;

    private String appId ;

    private int index;

    AppDbindex(String appId, int index){
        this.appId = appId;
        this.index = index;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
