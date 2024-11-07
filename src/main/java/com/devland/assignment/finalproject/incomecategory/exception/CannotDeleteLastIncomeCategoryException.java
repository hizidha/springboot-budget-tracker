package com.devland.assignment.finalproject.incomecategory.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class CannotDeleteLastIncomeCategoryException extends RuntimeException {
    public CannotDeleteLastIncomeCategoryException(String message) {
        super(message);
    }
}