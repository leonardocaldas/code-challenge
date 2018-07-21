package com.github.leonardocaldas.n26codechallenge.representation;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class Transaction {
    @NotNull
    private Double amount;

    @NotNull
    private Long timestamp;

    public Long getTimestampInSeconds() {
        return timestamp / 1000;
    }
}
