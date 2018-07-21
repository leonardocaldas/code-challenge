package com.github.leonardocaldas.n26codechallenge.model;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class Transaction {
    @NotNull
    private Double amount;

    @NotNull
    private Long timestamp;
}
