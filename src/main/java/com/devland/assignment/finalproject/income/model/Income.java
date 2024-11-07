package com.devland.assignment.finalproject.income.model;

import com.devland.assignment.finalproject.applicationuser.model.ApplicationUser;
import com.devland.assignment.finalproject.income.model.dto.IncomeResponseDTO;
import com.devland.assignment.finalproject.incomecategory.model.IncomeCategory;
import com.devland.assignment.finalproject.incomecategory.model.dto.IncomeCategoryResponseDTO;
import com.devland.assignment.finalproject.transactionhistory.model.TransactionHistory;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Income {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    private BigDecimal amount;

    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "income_category_id", nullable = false)
    private IncomeCategory incomeCategory;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private ApplicationUser user;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "transaction_history_id")
    private TransactionHistory transactionHistory;

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

    public IncomeResponseDTO convertToResponse() {
        IncomeCategoryResponseDTO incomeCategoryResponseDTO = this.incomeCategory.convertToResponse();

        return IncomeResponseDTO.builder()
                .id(this.id)
                .description(this.description)
                .amount(this.amount)
                .date(this.date)
                .category(incomeCategoryResponseDTO)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .build();
    }
}