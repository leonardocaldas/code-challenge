package com.github.leonardocaldas.n26codechallenge.web;

import com.github.leonardocaldas.n26codechallenge.representation.TransactionStatistics;
import com.github.leonardocaldas.n26codechallenge.service.StatisticService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/statistics")
public class StatisticController {

    private StatisticService statisticService;

    public StatisticController(StatisticService statisticService) {
        this.statisticService = statisticService;
    }

    @GetMapping
    public TransactionStatistics get() {
        return statisticService.getStatistics();
    }
}
