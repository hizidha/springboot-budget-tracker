package com.devland.assignment.finalproject.transactionhistory.model.dto;

import com.devland.assignment.finalproject.transactionhistory.model.TransactionType;
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
public class TransactionHistoryRequestDTO {
    @NotNull(message = "Transaction Type is required")
    private TransactionType type;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Amount is required")
    private BigDecimal amount;

    @NotNull(message = "Date is required")
    private LocalDate date;

    @NotNull(message = "Category ID is required")
    @Positive(message = "Category ID must be positive number or not zero")
    private Long categoryId;

    private Timestamp createdAt;
    private Timestamp updatedAt;
}