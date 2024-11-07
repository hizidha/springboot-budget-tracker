package com.devland.assignment.finalproject.transactionhistory.model;

import com.devland.assignment.finalproject.applicationuser.model.ApplicationUser;
import com.devland.assignment.finalproject.expense.model.Expense;
import com.devland.assignment.finalproject.expense.model.dto.ExpenseResponseDTO;
import com.devland.assignment.finalproject.income.model.Income;
import com.devland.assignment.finalproject.income.model.dto.IncomeResponseDTO;
import com.devland.assignment.finalproject.transactionhistory.model.dto.TransactionHistoryResponseDTO;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "income_id", nullable = true)
    private Income income;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "expense_id", nullable = true)
    private Expense expense;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private ApplicationUser user;

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

    public TransactionHistoryResponseDTO convertToResponse() {
        IncomeResponseDTO incomeResponseDTO = null;
        ExpenseResponseDTO expenseResponseDTO = null;

        if (this.income != null) {
            incomeResponseDTO = this.income.convertToResponse();
        }

        if (this.expense != null) {
            expenseResponseDTO = this.expense.convertToResponse();
        }

        return TransactionHistoryResponseDTO.builder()
                .id(this.id)
                .type(this.type)
                .income(incomeResponseDTO)
                .expense(expenseResponseDTO)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .build();
    }
}