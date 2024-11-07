package com.devland.assignment.finalproject.financialgoal.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class FinancialGoalAlreadyExist extends RuntimeException {
    public FinancialGoalAlreadyExist(String message) {
        super(message);
    }
}