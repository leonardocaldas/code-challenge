package com.github.leonardocaldas.n26codechallenge.service.impl;

import com.github.leonardocaldas.n26codechallenge.exceptions.TransactionOutOfRangeException;
import com.github.leonardocaldas.n26codechallenge.model.TransactionAggregate;
import com.github.leonardocaldas.n26codechallenge.repository.TransactionAggregateRepository;
import com.github.leonardocaldas.n26codechallenge.representation.Transaction;
import com.github.leonardocaldas.n26codechallenge.service.TransactionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

import static com.github.leonardocaldas.n26codechallenge.util.TransactionThreshold.getThresholdInMillis;
import static com.github.leonardocaldas.n26codechallenge.util.TransactionThreshold.getThresholdInSeconds;

@Service
public class TransactionServiceImpl implements TransactionService {
    private TransactionAggregateRepository transactionAggregateRepository;
    private Long timeRange;

    public TransactionServiceImpl(
            TransactionAggregateRepository transactionRepository,
            @Value("${transaction.time-range}") Long timeRange
    ) {
        this.timeRange = timeRange;
        this.transactionAggregateRepository = transactionRepository;
    }

    @Override
    public void save(Transaction transaction) {
        long currentMillis = Instant.now().toEpochMilli();

        validateIfTransactionIsOutOfRange(transaction, currentMillis);

        Long index = getIndexFromTransaction(transaction);

        transactionAggregateRepository.compute(index, (aggr) -> Optional.ofNullable(aggr)
                .map(aggregate -> compute(currentMillis, aggregate, transaction))
                .orElseGet(() -> initializeAggregate(transaction)));
    }

    private TransactionAggregate initializeAggregate(Transaction transaction) {
        return TransactionAggregate.builder()
                .count(1L)
                .max(transaction.getAmount())
                .min(transaction.getAmount())
                .sum(transaction.getAmount())
                .timestampInSeconds(transaction.getTimestampInSeconds())
                .build();
    }

    private TransactionAggregate compute(Long currentMillis, TransactionAggregate aggregate, Transaction transaction) {
        long thresholdInSeconds = getThresholdInSeconds(currentMillis, timeRange);

        if (aggregate.getTimestampInSeconds() < thresholdInSeconds) {
            return initializeAggregate(transaction);
        }

        return TransactionAggregate.builder()
                .count(aggregate.getCount() + 1)
                .timestampInSeconds(aggregate.getTimestampInSeconds())
                .max(Math.max(transaction.getAmount(), aggregate.getMax()))
                .min(Math.min(transaction.getAmount(), aggregate.getMin()))
                .sum(transaction.getAmount() + aggregate.getSum())
                .build();
    }

    private Long getIndexFromTransaction(Transaction transaction) {
        return transaction.getTimestampInSeconds() % timeRange;
    }

    private void validateIfTransactionIsOutOfRange(Transaction transaction, Long currentMillis) {
        long threshold = getThresholdInMillis(currentMillis, timeRange);

        if (transaction.getTimestamp() < threshold || transaction.getTimestamp() > currentMillis) {
            throw new TransactionOutOfRangeException();
        }
    }
}
