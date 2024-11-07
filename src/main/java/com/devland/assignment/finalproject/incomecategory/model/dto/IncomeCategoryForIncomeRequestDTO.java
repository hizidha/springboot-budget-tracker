package com.devland.assignment.finalproject.incomecategory.model.dto;

import com.devland.assignment.finalproject.incomecategory.model.IncomeCategory;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncomeCategoryForIncomeRequestDTO {
    @Positive(message = "ID must be positive number or not zero")
    @NotNull(message = "ID is required")
    private Long id;

    public IncomeCategory convertToEntity() {
        return IncomeCategory.builder()
                .id(this.id)
                .build();
    }
}