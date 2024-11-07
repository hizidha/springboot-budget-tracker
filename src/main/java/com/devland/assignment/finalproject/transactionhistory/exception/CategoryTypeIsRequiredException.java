package com.devland.assignment.finalproject.transactionhistory.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class CategoryTypeIsRequiredException extends RuntimeException {
    public CategoryTypeIsRequiredException(String message) {
        super(message);
    }
}