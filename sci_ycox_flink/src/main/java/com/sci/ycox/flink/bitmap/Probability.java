package com.sci.ycox.flink.bitmap;

import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Probability {
    public static void main (String[] arg){
        SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd:QQ");
        System.out.println(ymd.format(new Date()));
        int n = 1;
        for (int i = 0; i < 9; i++) {
            n *= 10;
//            testHyperLogLog(n, i);
        }
    }

    //测试n个元素的集合
    public static void testHyperLogLog(int n, int nu) {
        System.out.println("n = " + n);
        long t1 = System.currentTimeMillis();
        HyperLogLog hyperLogLog = new HyperLogLog();
        Set<Integer> s = new HashSet<>();
        Random random = new Random();
        for (int i = 0; i < n; i++) {
            int number = random.nextInt();
            hyperLogLog.hllAdd(number);
            if (nu < 8) {
                s.add(number);
            }

        }
        System.out.println("【" + (System.currentTimeMillis() - t1) + "ms】【"+ n + "】hyperLogLog count = 【" + hyperLogLog.hllCount()
        + "】hashset count = 【" + s.size()
        + "】error rate = 【" + Math.abs((double) hyperLogLog.hllCount() / n - 1) + "】");
    }
}
