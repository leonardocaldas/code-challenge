package com.github.leonardocaldas.n26codechallenge.service;

import com.github.leonardocaldas.n26codechallenge.representation.Transaction;
import com.github.leonardocaldas.n26codechallenge.representation.TransactionStatistics;

public interface TransactionService {
    void save(Transaction transaction);

    TransactionStatistics get();
}
