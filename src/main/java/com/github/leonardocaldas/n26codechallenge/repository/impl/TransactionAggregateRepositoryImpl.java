package com.github.leonardocaldas.n26codechallenge.repository.impl;

import com.github.leonardocaldas.n26codechallenge.model.TransactionAggregate;
import com.github.leonardocaldas.n26codechallenge.repository.TransactionAggregateRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.UnaryOperator;

@Repository
public class TransactionAggregateRepositoryImpl implements TransactionAggregateRepository {
    private final ConcurrentHashMap<Long, TransactionAggregate> aggregates;

    public TransactionAggregateRepositoryImpl(@Value("${transaction.time-range}") Integer timeRange) {
        aggregates = new ConcurrentHashMap<>(timeRange);
    }

    public TransactionAggregate compute(Long id, UnaryOperator<TransactionAggregate> function) {
        return aggregates.compute(id, (k, value) -> function.apply(value));
    }

    @Override
    public List<TransactionAggregate> findAll() {
        return new ArrayList<>(aggregates.values());
    }

    @Override
    public void deleteAll() {
        aggregates.clear();
    }
}
