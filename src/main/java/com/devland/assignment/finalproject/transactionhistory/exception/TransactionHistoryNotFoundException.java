package com.devland.assignment.finalproject.transactionhistory.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class TransactionHistoryNotFoundException extends RuntimeException {
    public TransactionHistoryNotFoundException(String message) {
        super(message);
    }
}