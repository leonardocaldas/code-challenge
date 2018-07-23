package com.github.leonardocaldas.n26codechallenge.representation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    @NotNull(message = "{amount.required}")
    private Double amount;

    @NotNull(message = "{timestamp.required}")
    private Long timestamp;

    @JsonIgnore
    public Long getTimestampInSeconds() {
        return timestamp / 1000;
    }
}
