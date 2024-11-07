package com.devland.assignment.finalproject.expensecategory.model.dto;

import com.devland.assignment.finalproject.expensecategory.model.ExpenseCategory;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseCategoryRequestDTO {
    @NotBlank(message = "Category Name is required")
    private String name;

    private Timestamp createdAt;
    private Timestamp updatedAt;

    public ExpenseCategory convertToEntity() {
        return ExpenseCategory.builder()
                .name(this.name)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .build();
    }
}