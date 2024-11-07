package com.devland.assignment.finalproject.expense.model;

import com.devland.assignment.finalproject.applicationuser.model.ApplicationUser;
import com.devland.assignment.finalproject.expense.model.dto.ExpenseResponseDTO;
import com.devland.assignment.finalproject.expensecategory.model.ExpenseCategory;
import com.devland.assignment.finalproject.expensecategory.model.dto.ExpenseCategoryResponseDTO;
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
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    private BigDecimal amount;

    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "expense_category_id", nullable = false)
    private ExpenseCategory expenseCategory;

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


    public ExpenseResponseDTO convertToResponse() {
        ExpenseCategoryResponseDTO expenseCategoryResponseDTO = this.expenseCategory.convertToResponse();

        return ExpenseResponseDTO.builder()
                .id(this.id)
                .description(this.description)
                .amount(this.amount)
                .date(this.date)
                .expenseCategory(expenseCategoryResponseDTO)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .build();
    }
}