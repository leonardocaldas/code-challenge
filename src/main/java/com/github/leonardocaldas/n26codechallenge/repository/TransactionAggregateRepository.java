package com.github.leonardocaldas.n26codechallenge.repository;

import com.github.leonardocaldas.n26codechallenge.model.TransactionAggregate;

import java.util.List;
import java.util.Optional;

public interface TransactionAggregateRepository {
    void save(Long id, TransactionAggregate transaction);

    Optional<TransactionAggregate> find(Long id);

    List<TransactionAggregate> findAll();
}