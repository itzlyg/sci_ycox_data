package com.sci.ycox.kafka.bean;

import com.sci.ycox.kafka.util.RandomSource;

import java.sql.Timestamp;

public class SourceEntity {

	private String r2;

	private String pvi;

	private String remoteip;

    /** dm */
    private String dm;

    /** scl */
    private String scl;

    /** 分辨率 */
    private String scr;

    /** 城市 */
    private String city;

    /** 省份 */
    private String province;

    /** 国家 */
    private String country;

	private String ty;

	private String url;

	private String lg;

	private String tz;

	private String ext;

	private String sex;

	private int age;

	private long random;

	/** 生成的pgv_si */
	private String si;


	private Timestamp ttime;

	public void create(){
        this.r2 = RandomSource.appId();
        this.remoteip = RandomSource.ip();
        this.dm = "localhost:8000";
        this.scl = "24-bit";
        this.scr = "1440x900";
        this.city = "武汉";
        this.province = "湖北";
        this.country = "中国";
        this.url = RandomSource.url();
        this.lg = RandomSource.lg();
        this.tz = RandomSource.tz();
        this.ext = RandomSource.version();
        this.sex = RandomSource.sex();
        this.age = RandomSource.age();
        this.random = System.currentTimeMillis() - 47606400000L;
        this.ttime = new Timestamp(getRandom());
        this.ty = RandomSource.ty();
        this.pvi = RandomSource.pvi();
        this.si = RandomSource.si();
    }

    public String getR2() {
        return r2;
    }

    public void setR2(String r2) {
        this.r2 = r2;
    }

    public String getPvi() {
        return pvi;
    }

    public void setPvi(String pvi) {
        this.pvi = pvi;
    }

    public String getRemoteip() {
        return remoteip;
    }

    public void setRemoteip(String remoteip) {
        this.remoteip = remoteip;
    }

    public String getDm() {
        return dm;
    }

    public void setDm(String dm) {
        this.dm = dm;
    }

    public String getScl() {
        return scl;
    }

    public void setScl(String scl) {
        this.scl = scl;
    }

    public String getScr() {
        return scr;
    }

    public void setScr(String scr) {
        this.scr = scr;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getTy() {
        return ty;
    }

    public void setTy(String ty) {
        this.ty = ty;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLg() {
        return lg;
    }

    public void setLg(String lg) {
        this.lg = lg;
    }

    public String getTz() {
        return tz;
    }

    public void setTz(String tz) {
        this.tz = tz;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public long getRandom() {
        return random;
    }

    public void setRandom(long random) {
        this.random = random;
    }

    public Timestamp getTtime() {
        return ttime;
    }

    public void setTtime(Timestamp ttime) {
        this.ttime = ttime;
    }

    public String getSi() {
        return si;
    }

    public void setSi(String si) {
        this.si = si;
    }
}
