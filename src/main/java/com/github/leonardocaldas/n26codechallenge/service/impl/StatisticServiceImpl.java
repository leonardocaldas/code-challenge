package com.github.leonardocaldas.n26codechallenge.service.impl;

import com.github.leonardocaldas.n26codechallenge.exceptions.TransactionOutOfRangeException;
import com.github.leonardocaldas.n26codechallenge.representation.Transaction;
import com.github.leonardocaldas.n26codechallenge.model.TransactionAggregate;
import com.github.leonardocaldas.n26codechallenge.representation.TransactionStatistics;
import com.github.leonardocaldas.n26codechallenge.repository.TransactionAggregateRepository;
import com.github.leonardocaldas.n26codechallenge.service.TransactionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class TransactionServiceImpl implements TransactionService {
    private TransactionAggregateRepository transactionAggregateRepository;
    private Long timeRange;

    public TransactionServiceImpl(
            TransactionAggregateRepository transactionRepository,
            @Value("transaction.time-range") Long timeRange
    ) {
        this.timeRange = timeRange;
        this.transactionAggregateRepository = transactionRepository;
    }

    public void save(Transaction transaction) {
        long currentMillis = Instant.now().toEpochMilli();

        validateIfTransactionIsOutOfRange(transaction, currentMillis);

        Long index = getIndexFromTransaction(transaction);

        TransactionAggregate transactionAggregate = transactionAggregateRepository.find(index)
                .map(aggregate -> compute(aggregate, transaction))
                .orElse(initializeAggregate(transaction));

        transactionAggregateRepository.save(index, transactionAggregate);
    }

    public TransactionStatistics get() {
        long currentMillis = Instant.now().toEpochMilli();
        long threshold = getTimeThreshold(currentMillis);

        TransactionStatistics statistics = new TransactionStatistics();

        transactionAggregateRepository.findAll().stream()
                .filter(aggregate -> aggregate.getTransactionTime() > threshold)
                .forEach(aggregate -> {
                    statistics.setCount(statistics.getCount() + aggregate.getCount());
                    statistics.setMin(Math.min(statistics.getMin(), aggregate.getMin()));
                    statistics.setMax(Math.min(statistics.getMax(), aggregate.getMax()));
                    statistics.setSum(statistics.getSum() + aggregate.getSum());
                    statistics.setAvg(statistics.getSum() / aggregate.getCount());
                });

        return statistics;
    }

    private TransactionAggregate initializeAggregate(Transaction transaction) {
        return TransactionAggregate.builder()
                .count(1L)
                .max(transaction.getAmount())
                .min(transaction.getAmount())
                .sum(transaction.getAmount())
                .transactionTime(transaction.getTimestamp() / 1000)
                .build();
    }

    private TransactionAggregate compute(TransactionAggregate aggregate, Transaction transaction) {
        return TransactionAggregate.builder()
                .count(aggregate.getCount() + 1)
                .max(Math.max(transaction.getAmount(), aggregate.getMax()))
                .min(Math.min(transaction.getAmount(), aggregate.getMin()))
                .sum(transaction.getAmount() + aggregate.getSum())
                .build();
    }

    private Long getIndexFromTransaction(Transaction transaction) {
        return (transaction.getTimestamp() / 1000) % timeRange;
    }

    private void validateIfTransactionIsOutOfRange(Transaction transaction, Long currentMillis) {
        long threshold = getTimeThreshold(currentMillis);

        if (transaction.getTimestamp() < threshold) {
            throw new TransactionOutOfRangeException();
        }
    }

    private Long getTimeThreshold(Long currentMillis) {
        return currentMillis - (timeRange * 1000);
    }
}
