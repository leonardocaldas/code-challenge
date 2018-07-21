package com.github.leonardocaldas.n26codechallenge.repository.impl;

import com.github.leonardocaldas.n26codechallenge.model.TransactionAggregate;
import com.github.leonardocaldas.n26codechallenge.repository.TransactionAggregateRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class TransactionAggregateRepositoryImpl implements TransactionAggregateRepository {
    private final ConcurrentHashMap<Long, TransactionAggregate> aggregates;

    public TransactionAggregateRepositoryImpl(@Value("${transaction.time-range}") Integer timeRange) {
        this.aggregates = new ConcurrentHashMap<>(timeRange);
    }

    @Override
    public void save(Long id, TransactionAggregate transactionAggregate) {
        this.aggregates.put(id, transactionAggregate);
    }

    @Override
    public Optional<TransactionAggregate> find(Long id) {
        return Optional.ofNullable(this.aggregates.get(id));
    }

    @Override
    public List<TransactionAggregate> findAll() {
        return new ArrayList<>(aggregates.values());
    }
}
