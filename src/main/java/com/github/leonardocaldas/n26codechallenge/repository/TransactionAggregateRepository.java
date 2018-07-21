package com.github.leonardocaldas.n26codechallenge.repository;

import com.github.leonardocaldas.n26codechallenge.model.TransactionAggregate;

import java.util.Optional;

public interface TransactionRepository {
    void save(Long id, TransactionAggregate transaction);

    Optional<TransactionAggregate> find(Long id);

    void findAll();
}
