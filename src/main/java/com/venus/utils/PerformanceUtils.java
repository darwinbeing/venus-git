package com.venus.utils;

/**
 * Created by erix-mac on 16/1/26.
 */
public final class PerformanceUtils {

    public static long beginTime(String methodName){
        long begin = System.currentTimeMillis();
        System.out.println(">>>>> Begin " + methodName + " Time: " + begin);

        return begin;
    }

    public static long endTime(String methodName, long beginTime){
        long end = System.currentTimeMillis();

        long timeCost = (end - beginTime) / 1000;
        System.out.println(">>>>> End " + methodName + " Total Time: " + timeCost + " s");

        return end;
    }
}
