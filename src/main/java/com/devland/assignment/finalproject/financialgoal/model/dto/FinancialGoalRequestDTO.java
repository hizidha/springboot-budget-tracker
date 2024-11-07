package com.devland.assignment.finalproject.financialgoal.model.dto;

import com.devland.assignment.finalproject.financialgoal.model.FinancialGoal;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialGoalRequestDTO {
    @NotBlank(message = "Financial Goal Name is required")
    private String name;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount can't zero")
    private BigDecimal goalAmount;

    private Timestamp createdAt;
    private Timestamp updatedAt;

    public FinancialGoal convertToEntity() {
        Double percentage = 0.0;

        return FinancialGoal.builder()
                .name(this.name)
                .goalAmount(this.goalAmount)
                .percentage(percentage)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .build();
    }
}