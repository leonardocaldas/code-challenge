package com.github.leonardocaldas.n26codechallenge.representation.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@Builder
@JsonInclude(NON_NULL)
public class ErrorMessageResponse {
    private ErrorCode errorCode;
    private String message;
    private List<String> fieldErrors;
}
