package com.github.leonardocaldas.n26codechallenge.service;

import com.github.leonardocaldas.n26codechallenge.exceptions.TransactionInvalidException;
import com.github.leonardocaldas.n26codechallenge.model.Transaction;
import com.github.leonardocaldas.n26codechallenge.model.TransactionBucket;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class StatisticService {

    private ConcurrentHashMap<Long, TransactionBucket> buckets;

    public void create(Transaction transaction) {
        validateIfTransactionTooOld(transaction);


    }

    public void cleanUp() {

    }

    private void validateIfTransactionTooOld(Transaction transaction) {
        long currentMillis = Instant.now().toEpochMilli();
        long threshold = currentMillis - 60000;

        if (transaction.getTimestamp() < threshold) {
            throw new TransactionInvalidException();
        }
    }
}
