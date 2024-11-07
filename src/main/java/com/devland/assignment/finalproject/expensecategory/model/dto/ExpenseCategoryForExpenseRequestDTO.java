package com.devland.assignment.finalproject.expensecategory.model.dto;

import com.devland.assignment.finalproject.expensecategory.model.ExpenseCategory;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseCategoryForExpenseRequestDTO {
    @Positive(message = "ID must be positive number or not zero")
    @NotNull(message = "ID is required")
    private Long id;

    public ExpenseCategory convertToEntity() {
        return ExpenseCategory.builder()
                .id(this.id)
                .build();
    }
}