package com.github.leonardocaldas.n26codechallenge.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class IntegratedTestUtil {
    @Value("${transaction.time-range}")
    private Long timeRange;

    public Long getCurrentTimestamp() {
        return Instant.now().toEpochMilli();
    }

    public Long getOutOfRangeTimestamp() {
        return getCurrentTimestamp() - ((timeRange + 1) * 1000);
    }
}
