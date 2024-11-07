package com.devland.assignment.finalproject.budget.model;

import com.devland.assignment.finalproject.applicationuser.model.ApplicationUser;
import com.devland.assignment.finalproject.budget.model.dto.BudgetResponseDTO;
import com.devland.assignment.finalproject.expensecategory.model.ExpenseCategory;
import com.devland.assignment.finalproject.expensecategory.model.dto.ExpenseCategoryResponseDTO;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.YearMonth;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Budget {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer month;

    private Integer year;

    private BigDecimal totalBudget;

    private BigDecimal remainingBudget;

    @Enumerated(EnumType.STRING)
    private BudgetStatus status;

    @ManyToOne
    @JoinColumn(name = "expense_category_id", nullable = false)
    private ExpenseCategory category;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private ApplicationUser user;

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

    public BudgetResponseDTO convertToResponse() {
        ExpenseCategoryResponseDTO expenseCategoryResponseDTO = this.category.convertToResponse();

        return BudgetResponseDTO.builder()
                .id(this.id)
                .year(this.year)
                .month(this.month)
                .totalBudget(this.totalBudget)
                .remainingBudget(this.remainingBudget)
                .category(expenseCategoryResponseDTO)
                .status(this.status)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .build();
    }
}