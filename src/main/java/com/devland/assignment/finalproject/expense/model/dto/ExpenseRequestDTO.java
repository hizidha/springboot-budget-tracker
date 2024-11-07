package com.devland.assignment.finalproject.expense.model.dto;

import com.devland.assignment.finalproject.expense.model.Expense;
import com.devland.assignment.finalproject.expensecategory.model.ExpenseCategory;
import com.devland.assignment.finalproject.expensecategory.model.dto.ExpenseCategoryForExpenseRequestDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseRequestDTO {
    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount can't zero")
    private BigDecimal amount;

    @NotNull(message = "Date is required")
    private LocalDate date;

    @Valid
    private ExpenseCategoryForExpenseRequestDTO expenseCategory;

    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Expense convertToEntity(){
        ExpenseCategory existingExpenseCategory = this.expenseCategory.convertToEntity();

        return Expense.builder()
                .description(this.description)
                .amount(this.amount)
                .date(this.date)
                .expenseCategory(existingExpenseCategory)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .build();
    }
}