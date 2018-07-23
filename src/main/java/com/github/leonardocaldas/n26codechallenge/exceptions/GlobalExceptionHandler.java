package com.github.leonardocaldas.n26codechallenge.exceptions;

import com.github.leonardocaldas.n26codechallenge.representation.error.ErrorCode;
import com.github.leonardocaldas.n26codechallenge.representation.error.ErrorMessageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.stream.Collectors;

import static com.github.leonardocaldas.n26codechallenge.representation.error.ErrorCode.HTTP_METHOD_NOT_SUPPORTED;
import static com.github.leonardocaldas.n26codechallenge.representation.error.ErrorCode.MEDIA_TYPE_NOT_SUPPORTED;
import static com.github.leonardocaldas.n26codechallenge.representation.error.ErrorCode.PAYLOAD_INVALID;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@ControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    private MessageSource messageSource;

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ErrorMessageResponse handleMethodArgumentNotValidErros(MethodArgumentNotValidException e) {
        return ErrorMessageResponse.builder()
                .errorCode(PAYLOAD_INVALID)
                .message(getLocaleMessageFromErrorCode(PAYLOAD_INVALID))
                .fieldErrors(e.getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .collect(Collectors.toList()))
                .build();
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public ErrorMessageResponse handleJsonParseError(HttpMessageNotReadableException e) {
        return ErrorMessageResponse.builder()
                .errorCode(PAYLOAD_INVALID)
                .message(getLocaleMessageFromErrorCode(PAYLOAD_INVALID))
                .build();
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    public ErrorMessageResponse handleHttpRequestMethodNotSupportedError(HttpRequestMethodNotSupportedException e) {
        return ErrorMessageResponse.builder()
                .errorCode(HTTP_METHOD_NOT_SUPPORTED)
                .message(getLocaleMessageFromErrorCode(HTTP_METHOD_NOT_SUPPORTED, e.getMethod()))
                .build();
    }

    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    @ResponseBody
    protected ErrorMessageResponse handleAllExceptions(Exception e) {
        return ErrorMessageResponse.builder()
                .errorCode(ErrorCode.INTERNAL_SERVER_ERROR)
                .message(getLocaleMessageFromErrorCode(ErrorCode.INTERNAL_SERVER_ERROR))
                .build();
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseBody
    protected ErrorMessageResponse handleHttpMediaTypeNotSupportedError(HttpMediaTypeNotSupportedException e) {
        return ErrorMessageResponse.builder()
                .errorCode(MEDIA_TYPE_NOT_SUPPORTED)
                .message(getLocaleMessageFromErrorCode(MEDIA_TYPE_NOT_SUPPORTED, e.getContentType()))
                .build();
    }

    private String getLocaleMessageFromErrorCode(ErrorCode errorCode, Object... params) {
        return messageSource.getMessage(errorCode.getCode(), params, LocaleContextHolder.getLocale());
    }
}
