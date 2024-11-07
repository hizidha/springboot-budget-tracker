package com.devland.assignment.finalproject.incomecategory.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class IncomeCategoryNotFoundException extends RuntimeException {
    public IncomeCategoryNotFoundException(String message) {
        super(message);
    }
}