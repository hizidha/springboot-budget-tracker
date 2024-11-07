package com.devland.assignment.finalproject.incomecategory.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class IncomeCategoryAlreadyExistException extends RuntimeException {
    public IncomeCategoryAlreadyExistException(String message) {
        super(message);
    }
}