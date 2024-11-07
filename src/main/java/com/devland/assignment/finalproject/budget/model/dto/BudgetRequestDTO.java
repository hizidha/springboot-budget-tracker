package com.devland.assignment.finalproject.budget.model.dto;

import com.devland.assignment.finalproject.budget.model.Budget;
import com.devland.assignment.finalproject.expensecategory.model.ExpenseCategory;
import com.devland.assignment.finalproject.expensecategory.model.dto.ExpenseCategoryForExpenseRequestDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.YearMonth;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetRequestDTO {
    @NotNull(message = "Year is required")
    @Positive(message = "Year can't zero")
    private Integer year;

    @NotNull(message = "Month is required")
    @Min(value = 1, message = "Month must be between 1 and 12")
    @Max(value = 12, message = "Month must be between 1 and 12")
    private Integer month;

    @NotNull(message = "Total Budget is required")
    @Positive(message = "Total Budget can't zero")
    private BigDecimal totalBudget;

    @Valid
    private ExpenseCategoryForExpenseRequestDTO category;

    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Budget convertToEntity() {
        ExpenseCategory existingExpenseCategory = this.category.convertToEntity();

        return Budget.builder()
                .year(this.year)
                .month(this.month)
                .totalBudget(this.totalBudget)
                .category(existingExpenseCategory)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .build();
    }
}