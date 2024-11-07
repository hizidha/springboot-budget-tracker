package com.devland.assignment.finalproject.expensecategory.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class CannotDeleteLastExpenseCategoryException extends RuntimeException {
    public CannotDeleteLastExpenseCategoryException(String message) {
        super(message);
    }
}