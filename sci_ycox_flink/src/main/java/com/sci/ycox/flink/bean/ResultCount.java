package com.sci.ycox.flink.bean;

public class ResultCount {
    private String name;
    private Long counts;
    public ResultCount(){

    }
    public ResultCount(String name, Long counts){
        this.counts = counts;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCounts() {
        return counts;
    }

    public void setCounts(Long counts) {
        this.counts = counts;
    }

    @Override
    public String toString() {
        return "ResultCount{" +
                "name='" + name + '\'' +
                ", counts=" + counts +
                '}';
    }
}
