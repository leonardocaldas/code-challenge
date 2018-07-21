package com.github.leonardocaldas.n26codechallenge.web;

import com.github.leonardocaldas.n26codechallenge.model.Transaction;
import com.github.leonardocaldas.n26codechallenge.service.TransactionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/statistics")
public class StatisticController {

    private TransactionService statisticService;

    public StatisticController(TransactionService statisticService) {
        this.statisticService = statisticService;
    }

    @PostMapping
    public void create(@Valid Transaction transaction) {
        statisticService.create(transaction);
    }

    @GetMapping
    public void get() {

    }
}
