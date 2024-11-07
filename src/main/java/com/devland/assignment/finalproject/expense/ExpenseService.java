package com.devland.assignment.finalproject.expense;

import com.devland.assignment.finalproject.applicationuser.ApplicationUserService;
import com.devland.assignment.finalproject.applicationuser.model.ApplicationUser;
import com.devland.assignment.finalproject.budget.BudgetService;
import com.devland.assignment.finalproject.budget.model.Budget;
import com.devland.assignment.finalproject.budget.model.BudgetStatus;
import com.devland.assignment.finalproject.expense.exception.ExpenseNotFoundException;
import com.devland.assignment.finalproject.expense.exception.InsufficientBalanceException;
import com.devland.assignment.finalproject.expense.model.Expense;
import com.devland.assignment.finalproject.expensecategory.ExpenseCategoryService;
import com.devland.assignment.finalproject.expensecategory.model.ExpenseCategory;
import com.devland.assignment.finalproject.financialgoal.FinancialGoalService;
import com.devland.assignment.finalproject.financialgoal.model.FinancialGoal;
import com.devland.assignment.finalproject.transactionhistory.TransactionHistoryService;
import com.devland.assignment.finalproject.transactionhistory.model.TransactionHistory;
import com.devland.assignment.finalproject.transactionhistory.model.TransactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final BudgetService budgetService;
    private final FinancialGoalService financialGoalService;
    private final ExpenseCategoryService expenseCategoryService;
    private final ApplicationUserService applicationUserService;
    private final TransactionHistoryService transactionHistoryService;

    public Page<Expense> findAll(Optional<String> optionalDescription, String username, Integer month, Integer year, Pageable pageable) {
        ApplicationUser existingUser = this.applicationUserService.findByUsername(username);

        if (month == null || year == null) {
            LocalDate now = LocalDate.now();
            month = (month == null) ? now.getMonthValue() : month;
            year = (year == null) ? now.getYear() : year;
        }

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1);

        if (optionalDescription.isPresent()) {
            return this.expenseRepository.findAllByDescriptionContainsIgnoreCaseAndUserIdAndDateBetween(
                    optionalDescription.get(), existingUser.getId(), startDate, endDate, pageable);
        }

        return this.expenseRepository.findAllByUserIdAndDateBetween(
                existingUser.getId(), startDate, endDate, pageable);
    }

    public Expense findBy(String username, Long id) {
        ApplicationUser existingUser = this.applicationUserService.findByUsername(username);

        return expenseRepository.findByIdAndUserId(id, existingUser.getId())
                .orElseThrow(() -> new ExpenseNotFoundException("Expense with ID " + id + " not found"));
    }

    public Expense create(String username, Expense newExpense) {
        ApplicationUser existingUser = this.applicationUserService.findByUsername(username);
        ExpenseCategory expenseCategory = this.expenseCategoryService.findBy(username, newExpense.getExpenseCategory().getId());

        Budget budget = this.budgetService.findByCategoryIdAndMonthAndYear(
                username, expenseCategory.getId(), newExpense.getDate().getMonthValue(), newExpense.getDate().getYear());

        newExpense.setUser(existingUser);
        newExpense.setExpenseCategory(expenseCategory);

        if (existingUser.getTotalBalance().compareTo(newExpense.getAmount()) < 0) {
            throw new InsufficientBalanceException("Your balance is not sufficient for this expense.");
        }

        Expense savedExpense = this.expenseRepository.save(newExpense);
        this.updateBudgetAfterExpense(username, budget, newExpense.getAmount());

        TransactionHistory transactionHistory = TransactionHistory.builder()
                .type(TransactionType.EXPENSE)
                .expense(savedExpense)
                .user(existingUser)
                .build();

        this.transactionHistoryService.create(transactionHistory);

        this.updateTotalBalance(existingUser, savedExpense.getAmount().negate());
        this.updateFinancialGoals(existingUser);

        return savedExpense;
    }

    public Expense update(String username, Expense updatedExpense) {
        ApplicationUser existingUser = this.applicationUserService.findByUsername(username);
        Expense existingExpense = this.findBy(username, updatedExpense.getId());

        updatedExpense.setId(existingExpense.getId());
        updatedExpense.setExpenseCategory(
                this.expenseCategoryService.findBy(username, updatedExpense.getExpenseCategory().getId())
        );

        BigDecimal amountChange = updatedExpense.getAmount().subtract(existingExpense.getAmount());

        if (existingUser.getTotalBalance().add(amountChange).compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientBalanceException("Your balance is not sufficient for this expense.");
        }

        this.updateTotalBalance(existingUser, amountChange);

        TransactionHistory existingTransactionHistory = updatedExpense.getTransactionHistory();
        if (existingTransactionHistory != null) {
            existingTransactionHistory.setExpense(existingExpense);
            this.transactionHistoryService.update(existingTransactionHistory);
        }

        Expense savedExpense = this.expenseRepository.save(existingExpense);

        this.updateFinancialGoals(existingUser);

        return savedExpense;
    }

    public void delete(String username, Long id) {
        Expense existingExpense = this.findBy(username, id);
        Budget budget = budgetService.findByCategoryIdAndMonthAndYear(
                username, existingExpense.getExpenseCategory().getId(), existingExpense.getDate().getMonthValue(), existingExpense.getDate().getYear());

        BigDecimal amountChange = existingExpense.getAmount().negate();
        this.updateBudgetAfterExpense(username, budget, amountChange);

        this.expenseRepository.delete(existingExpense);
        ApplicationUser existingUser = existingExpense.getUser();
        this.updateTotalBalance(existingUser, amountChange);
        this.updateFinancialGoals(existingUser);
    }

    private void updateBudgetAfterExpense(String username, Budget budget, BigDecimal amountChange) {
        if (budget != null) {
            budget.setRemainingBudget(budget.getRemainingBudget().subtract(amountChange));
            budget.setStatus(budget.getRemainingBudget().compareTo(BigDecimal.ZERO) < 0 ?
                    BudgetStatus.EXCEEDED_BUDGET : BudgetStatus.WITHIN_BUDGET);
            this.budgetService.update(username, budget);
        }
    }

    private void updateTotalBalance(ApplicationUser user, BigDecimal amountChange) {
        user.setTotalBalance(user.getTotalBalance().add(amountChange));
        this.applicationUserService.save(user);
    }

    private void updateFinancialGoals(ApplicationUser user) {
        List<FinancialGoal> financialGoals = this.financialGoalService.findByUserId(user.getId());

        for (FinancialGoal goal : financialGoals) {
            Double percentage = financialGoalService.calculatePercentage(goal, user);
            goal.setPercentage(percentage);
            financialGoalService.save(goal);
        }
    }
}