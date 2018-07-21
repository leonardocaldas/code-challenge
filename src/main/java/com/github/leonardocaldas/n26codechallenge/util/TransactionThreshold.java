package com.github.leonardocaldas.n26codechallenge.helper;

public class TimestampThreshold {
    public static Long getThresholdInMillis(Long currentMillis, Long timeRange) {
        return currentMillis - (timeRange * 1000);
    }

    public static Long getThresholdInSeconds(Long currentMillis, Long timeRange) {
        return getThresholdInMillis(currentMillis, timeRange) / 1000;
    }
}
