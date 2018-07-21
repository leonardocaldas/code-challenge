package com.github.leonardocaldas.n26codechallenge.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransactionBucket {
    private Double min;
    private Double max;
    private Double sum;
    private Long count;
}
