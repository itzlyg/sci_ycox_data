package com.sci.ycox.flink.util;

import org.apache.commons.lang3.StringUtils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @Author: xyb
 * @Description:
 * @Date: 2023-05-13 上午 01:20
 **/
public class DateUtil {
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final String HH = "HH-";
    public static String formatDateTime(Timestamp timestamp, String pattern) {
        if (timestamp == null) {
            return null;
        }
        return formatDateTime(timestamp.toLocalDateTime(), pattern);
    }
    public static String formatDateTime(LocalDateTime dateTime, String pattern) {
        if (dateTime == null) {
            return null;
        }
        if (StringUtils.isBlank(pattern)) {
            pattern = YYYY_MM_DD_HH_MM_SS;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return dateTime.format(formatter);
    }
}