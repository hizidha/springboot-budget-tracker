package com.devland.assignment.finalproject.expense.model.dto;

import com.devland.assignment.finalproject.expensecategory.model.dto.ExpenseCategoryResponseDTO;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseResponseDTO {
    private Long id;
    private String description;
    private BigDecimal amount;
    private LocalDate date;
    private ExpenseCategoryResponseDTO expenseCategory;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}