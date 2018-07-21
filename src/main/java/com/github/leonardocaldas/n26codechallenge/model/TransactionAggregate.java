package com.github.leonardocaldas.n26codechallenge.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransactionAggregate {
    private Double sum;
    private Double min;
    private Double max;
    private Long count;
    private Long timestampInSeconds;
}
