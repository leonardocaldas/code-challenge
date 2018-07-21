package com.github.leonardocaldas.n26codechallenge.service.impl;

import com.github.leonardocaldas.n26codechallenge.util.TransactionThreshold;
import com.github.leonardocaldas.n26codechallenge.repository.TransactionAggregateRepository;
import com.github.leonardocaldas.n26codechallenge.representation.TransactionStatistics;
import com.github.leonardocaldas.n26codechallenge.service.StatisticService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class StatisticServiceImpl implements StatisticService {
    private TransactionAggregateRepository transactionAggregateRepository;
    private Long timeRange;

    public StatisticServiceImpl(
            TransactionAggregateRepository transactionRepository,
            @Value("${transaction.time-range}") Long timeRange
    ) {
        this.timeRange = timeRange;
        this.transactionAggregateRepository = transactionRepository;
    }

    @Override
    public TransactionStatistics getStatistics() {
        long currentMillis = Instant.now().toEpochMilli();
        long thresholdInSeconds = TransactionThreshold.getThresholdInSeconds(currentMillis, timeRange);

        TransactionStatistics statistics = new TransactionStatistics();

        transactionAggregateRepository.findAll().stream()
                .filter(aggregate -> aggregate.getTimestampInSeconds() > thresholdInSeconds)
                .forEach(aggregate -> {
                    statistics.setMin(statistics.getCount() == 0 ? aggregate.getMin() : Math.min(statistics.getMin(), aggregate.getMin()));
                    statistics.setCount(statistics.getCount() + aggregate.getCount());
                    statistics.setMax(Math.max(statistics.getMax(), aggregate.getMax()));
                    statistics.setSum(statistics.getSum() + aggregate.getSum());
                    statistics.setAvg(statistics.getSum() / statistics.getCount());
                });

        return statistics;
    }
}
