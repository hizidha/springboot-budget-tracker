package com.devland.assignment.finalproject.budget.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class BudgetAlreadyExistException extends RuntimeException {
    public BudgetAlreadyExistException(String message) {
        super(message);
    }
}