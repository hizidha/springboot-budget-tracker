package com.devland.assignment.finalproject.applicationuser.model;

import com.devland.assignment.finalproject.authentication.model.dto.UserProfileResponseDTO;
import com.devland.assignment.finalproject.budget.model.Budget;
import com.devland.assignment.finalproject.expense.model.Expense;
import com.devland.assignment.finalproject.expensecategory.model.ExpenseCategory;
import com.devland.assignment.finalproject.financialgoal.model.FinancialGoal;
import com.devland.assignment.finalproject.income.model.Income;
import com.devland.assignment.finalproject.incomecategory.model.IncomeCategory;
import com.devland.assignment.finalproject.transactionhistory.model.TransactionHistory;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String email;

    private String username;

    private String password;

    private BigDecimal totalBalance;

    @OneToMany(mappedBy = "user")
    private List<IncomeCategory> incomeCategories;

    @OneToMany(mappedBy = "user")
    private List<ExpenseCategory> expenseCategories;

    @OneToMany(mappedBy = "user")
    private List<Income> incomes;

    @OneToMany(mappedBy = "user")
    private List<Expense> expenses;

    @OneToMany(mappedBy = "user")
    private List<FinancialGoal> financialGoals;

    @OneToMany(mappedBy = "user")
    private List<Budget> budgets;

    @OneToMany(mappedBy = "user")
    private List<TransactionHistory> transactionHistories;

    public UserProfileResponseDTO convertToResponse() {
        return UserProfileResponseDTO.builder()
                .username(this.username)
                .email(this.email)
                .name(this.name)
                .balance(this.totalBalance)
                .build();
    }
}