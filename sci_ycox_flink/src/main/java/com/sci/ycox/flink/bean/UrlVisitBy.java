package com.sci.ycox.flink.bean;

public class UrlVisitBy {

    private long start;

    private long end;

    private String v1;

    private long count;

    private String v2;

    public UrlVisitBy(long start, long end, String v1, long count, String v2) {
        this.start = start;
        this.end = end;
        this.v1 = v1;
        this.count = count;
        this.v2 = v2;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public String getV1() {
        return v1;
    }

    public void setV1(String v1) {
        this.v1 = v1;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public String getV2() {
        return v2;
    }

    public void setV2(String v2) {
        this.v2 = v2;
    }
}
