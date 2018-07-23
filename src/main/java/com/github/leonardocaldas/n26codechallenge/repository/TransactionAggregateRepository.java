package com.github.leonardocaldas.n26codechallenge.repository;

import com.github.leonardocaldas.n26codechallenge.model.TransactionAggregate;

import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;

public interface TransactionAggregateRepository {
    List<TransactionAggregate> findAll();

    TransactionAggregate compute(Long id, UnaryOperator<TransactionAggregate> function);

    void deleteAll();
}