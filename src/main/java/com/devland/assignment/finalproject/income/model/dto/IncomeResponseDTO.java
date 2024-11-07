package com.devland.assignment.finalproject.income.model.dto;

import com.devland.assignment.finalproject.incomecategory.model.dto.IncomeCategoryResponseDTO;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncomeResponseDTO {
    private Long id;
    private String description;
    private BigDecimal amount;
    private LocalDate date;
    private IncomeCategoryResponseDTO category;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}