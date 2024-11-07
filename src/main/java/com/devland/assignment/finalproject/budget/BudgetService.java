package com.devland.assignment.finalproject.budget;

import com.devland.assignment.finalproject.applicationuser.ApplicationUserService;
import com.devland.assignment.finalproject.applicationuser.model.ApplicationUser;
import com.devland.assignment.finalproject.budget.exception.BudgetAlreadyExistException;
import com.devland.assignment.finalproject.budget.exception.BudgetNotFoundException;
import com.devland.assignment.finalproject.budget.model.Budget;
import com.devland.assignment.finalproject.budget.model.BudgetStatus;
import com.devland.assignment.finalproject.expense.ExpenseRepository;
import com.devland.assignment.finalproject.expensecategory.ExpenseCategoryService;
import com.devland.assignment.finalproject.expensecategory.model.ExpenseCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BudgetService {
    private final BudgetRepository budgetRepository;
    private final ExpenseRepository expenseRepository;
    private final ExpenseCategoryService expenseCategoryService;
    private final ApplicationUserService applicationUserService;

    public Page<Budget> findAll(String username, Integer month, Integer year, Pageable pageable) {
        ApplicationUser user = this.applicationUserService.findByUsername(username);

        LocalDate now = LocalDate.now();
        int finalMonth = (month != null && month >= 1 && month <= 12) ? month : now.getMonthValue();
        int finalYear = (year != null && year > 0) ? year : now.getYear();

        return this.budgetRepository.findAllByUserIdAndMonthAndYear(user.getId(), finalMonth, finalYear, pageable);
    }

    public Budget findBy(String username, Long id) {
        ApplicationUser user = this.applicationUserService.findByUsername(username);
        return this.budgetRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new BudgetNotFoundException("Budget with ID " + id + " not found"));
    }

    public Budget findByCategoryId(String username, Long id) {
        ApplicationUser user = this.applicationUserService.findByUsername(username);
        return this.budgetRepository.findByCategoryIdAndUserId(id, user.getId())
                .orElseThrow(() -> new BudgetNotFoundException("Budget with ID " + id + " not found"));
    }

    public Budget findByCategoryIdAndMonthAndYear(String username, Long categoryId, int month, int year) {
        ApplicationUser user = this.applicationUserService.findByUsername(username);

        return this.budgetRepository
                .findByUserIdAndCategoryIdAndMonthAndYear(user.getId(), categoryId, month, year)
                .orElse(null);
    }

    public Budget create(String username, Budget budgetRequest) {
        ApplicationUser user = this.applicationUserService.findByUsername(username);

        ExpenseCategory existingCategory = this.expenseCategoryService
                .findBy(username, budgetRequest.getCategory().getId());
        budgetRequest.setCategory(existingCategory);

        this.verifyNoExistingBudget(user.getId(), budgetRequest);

        BigDecimal totalExpenses = this.calculateTotalExpenses(budgetRequest, user);
        Result result = this.calculateBudgetResult(budgetRequest, totalExpenses);

        budgetRequest.setRemainingBudget(result.remainingBudget());
        budgetRequest.setStatus(result.budgetStatus());
        budgetRequest.setUser(user);

        return this.budgetRepository.save(budgetRequest);
    }

    public Budget update(String username, Budget budgetUpdateRequest) {
        ApplicationUser user = this.applicationUserService.findByUsername(username);
        Budget currentBudget = this.findBy(username, budgetUpdateRequest.getId());

        ExpenseCategory existingCategory = this.expenseCategoryService.findBy(username, budgetUpdateRequest.getCategory().getId());
        budgetUpdateRequest.setCategory(existingCategory);

        if (this.isDuplicateBudget(user.getId(), budgetUpdateRequest, currentBudget.getId())) {
            throw new BudgetAlreadyExistException("Budget for this category: " + existingCategory.getName() +
                    " in month: " + budgetUpdateRequest.getMonth() + " and year: " + budgetUpdateRequest.getYear() + " already exists");
        }

        BigDecimal totalExpenses = this.calculateTotalExpenses(budgetUpdateRequest, user);
        Result result = this.calculateBudgetResult(budgetUpdateRequest, totalExpenses);

        budgetUpdateRequest.setRemainingBudget(result.remainingBudget());
        budgetUpdateRequest.setStatus(result.budgetStatus());
        budgetUpdateRequest.setUser(user);

        return this.budgetRepository.save(budgetUpdateRequest);
    }

    public void delete(String username, Long id) {
        this.budgetRepository.deleteById(this.findBy(username, id).getId());
    }

    private BigDecimal calculateTotalExpenses(Budget budget, ApplicationUser user) {
        BigDecimal totalExpenses = this.expenseRepository.sumAmountByUserIdAndMonthAndYear(
                user.getId(), budget.getMonth(), budget.getYear());

        return totalExpenses == null ? BigDecimal.ZERO : totalExpenses;
    }

    private Result calculateBudgetResult(Budget budget, BigDecimal totalExpenses) {
        BigDecimal remainingBudget = budget.getTotalBudget().subtract(totalExpenses);
        BudgetStatus budgetStatus = remainingBudget.compareTo(BigDecimal.ZERO) < 0
                ? BudgetStatus.EXCEEDED_BUDGET
                : BudgetStatus.WITHIN_BUDGET;

        return new Result(remainingBudget, budgetStatus);
    }

    private void verifyNoExistingBudget(Long userId, Budget budgetRequest) {
        if (this.budgetRepository.existsByUserIdAndCategoryIdAndMonthAndYear(
                userId, budgetRequest.getCategory().getId(), budgetRequest.getMonth(), budgetRequest.getYear())
        ) {
            throw new BudgetAlreadyExistException("Budget for this category: " + budgetRequest.getCategory().getName() +
                    " in month: " + budgetRequest.getMonth() + " and year: " + budgetRequest.getYear() + " already exists");
        }
    }

    private boolean isDuplicateBudget(Long userId, Budget budgetRequest, Long currentBudgetId) {
        Optional<Budget> duplicateBudget = this.budgetRepository
                .findByUserIdAndCategoryIdAndMonthAndYear(userId, budgetRequest.getCategory().getId(),
                        budgetRequest.getMonth(), budgetRequest.getYear());

        if (duplicateBudget.isPresent() && !duplicateBudget.get().getId().equals(currentBudgetId)) {
            return true;
        }

        Optional<Budget> conflictingBudget = this.budgetRepository
                .findByUserIdAndCategoryIdAndMonthAndYear(userId, budgetRequest.getCategory().getId(),
                        budgetRequest.getMonth(), budgetRequest.getYear());

        return conflictingBudget.isPresent() && !conflictingBudget.get().getId().equals(currentBudgetId);
    }

    private record Result(BigDecimal remainingBudget, BudgetStatus budgetStatus) {
    }
}