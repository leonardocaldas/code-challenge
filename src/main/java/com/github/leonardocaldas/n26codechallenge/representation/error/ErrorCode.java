package com.github.leonardocaldas.n26codechallenge.representation.error;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ErrorCode {
    MEDIA_TYPE_NOT_SUPPORTED("media.type.not.supported"),
    HTTP_METHOD_NOT_SUPPORTED("http.method.not.supported"),
    PAYLOAD_INVALID("payload.invalid"),
    INTERNAL_SERVER_ERROR("internal.server.error");

    private final String errorCode;

    ErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    @JsonValue
    public String getCode() {
        return this.errorCode;
    }
}
