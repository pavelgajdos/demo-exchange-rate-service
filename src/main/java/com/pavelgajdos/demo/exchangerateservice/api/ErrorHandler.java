package com.pavelgajdos.demo.exchangerateservice.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(value = ResponseStatusException.class)
    @ResponseBody
    public MyError handle(ResponseStatusException e) {
        HttpStatus status = (HttpStatus)e.getStatusCode();
        return new MyError(Instant.now(), status.value(), status.getReasonPhrase(), e.getReason());
    }

    public record MyError(
            Instant timestamp,
            int status,
            String error,
            String reason
    ) {
    }

}
