package com.sci.ycox.flink.util;

import org.apache.flink.table.functions.ScalarFunction;

import java.sql.Timestamp;

public class Utc2Local extends ScalarFunction {

    public Timestamp eval(Timestamp s) {
        long timestamp = s.getTime() + 28800000;
        return new Timestamp(timestamp);
    }

}
