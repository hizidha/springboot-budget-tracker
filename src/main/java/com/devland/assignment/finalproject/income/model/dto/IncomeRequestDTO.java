package com.devland.assignment.finalproject.income.model.dto;

import com.devland.assignment.finalproject.income.model.Income;
import com.devland.assignment.finalproject.incomecategory.model.IncomeCategory;
import com.devland.assignment.finalproject.incomecategory.model.dto.IncomeCategoryForIncomeRequestDTO;
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
public class IncomeRequestDTO {
    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount can't zero")
    private BigDecimal amount;

    @NotNull(message = "Date is required")
    private LocalDate date;

    @Valid
    private IncomeCategoryForIncomeRequestDTO incomeCategory;

    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Income convertToEntity(){
        IncomeCategory existingIncomeCategory = this.incomeCategory.convertToEntity();

        return Income.builder()
                .description(this.description)
                .amount(this.amount)
                .date(this.date)
                .incomeCategory(existingIncomeCategory)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .build();
    }
}