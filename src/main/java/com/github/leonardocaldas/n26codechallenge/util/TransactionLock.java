package com.github.leonardocaldas.n26codechallenge.util;

import java.util.concurrent.ConcurrentHashMap;

public class TransactionLock {
    private static ConcurrentHashMap<Long, Object> locks = new ConcurrentHashMap<>();

    public static Object getLockForKey(Long key) {
        return locks.computeIfAbsent(key, (k) -> new Object());
    }
}
