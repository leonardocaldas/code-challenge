package com.github.leonardocaldas.n26codechallenge.service.impl;

import com.github.leonardocaldas.n26codechallenge.repository.TransactionAggregateRepository;
import com.github.leonardocaldas.n26codechallenge.representation.TransactionStatistics;
import com.github.leonardocaldas.n26codechallenge.service.StatisticService;
import com.github.leonardocaldas.n26codechallenge.util.TransactionThreshold;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
                    statistics.setSum(round(statistics.getSum() + aggregate.getSum()));
                    statistics.setAvg(round(statistics.getSum() / statistics.getCount()));
                });

        return statistics;
    }
    private Double round(Double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.DOWN).doubleValue();
    }

}
