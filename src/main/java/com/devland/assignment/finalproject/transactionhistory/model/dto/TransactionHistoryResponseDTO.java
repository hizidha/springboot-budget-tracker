package com.devland.assignment.finalproject.transactionhistory.model.dto;

import com.devland.assignment.finalproject.expense.model.dto.ExpenseResponseDTO;
import com.devland.assignment.finalproject.income.model.dto.IncomeResponseDTO;
import com.devland.assignment.finalproject.transactionhistory.model.TransactionType;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionHistoryResponseDTO {
    private Long id;
    private TransactionType type;
    private IncomeResponseDTO income;
    private ExpenseResponseDTO expense;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}