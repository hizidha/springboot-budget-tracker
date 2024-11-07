package com.devland.assignment.finalproject.financialgoal.model.dto;

import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialGoalResponseDTO {
    private Long id;
    private String name;
    private BigDecimal goalAmount;
    private Double percentage;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}