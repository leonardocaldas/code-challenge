package com.github.leonardocaldas.n26codechallenge.service;

import com.github.leonardocaldas.n26codechallenge.representation.Transaction;

public interface TransactionService {
    void save(Transaction transaction);
}
