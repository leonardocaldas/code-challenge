package com.github.leonardocaldas.n26codechallenge.web;

import com.github.leonardocaldas.n26codechallenge.exceptions.TransactionOutOfRangeException;
import com.github.leonardocaldas.n26codechallenge.representation.Transaction;
import com.github.leonardocaldas.n26codechallenge.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody Transaction transaction) {
        try {
            transactionService.save(transaction);
            return new ResponseEntity<>(HttpStatus.CREATED);

        } catch (TransactionOutOfRangeException e) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }
}
