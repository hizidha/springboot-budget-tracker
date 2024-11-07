package com.devland.assignment.finalproject.budget.model.dto;

import com.devland.assignment.finalproject.budget.model.BudgetStatus;
import com.devland.assignment.finalproject.expensecategory.model.dto.ExpenseCategoryResponseDTO;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetResponseDTO {
    private Long id;
    private Integer year;
    private Integer month;
    private BigDecimal totalBudget;
    private BigDecimal remainingBudget;
    private BudgetStatus status;
    private ExpenseCategoryResponseDTO category;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}