package com.devland.assignment.finalproject.incomecategory.model.dto;

import com.devland.assignment.finalproject.incomecategory.model.IncomeCategory;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncomeCategoryRequestDTO {
    @NotBlank(message = "Category Name is required")
    private String name;

    private Timestamp createdAt;
    private Timestamp updatedAt;

    public IncomeCategory convertToEntity() {
        return IncomeCategory.builder()
                .name(this.name)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .build();
    }
}