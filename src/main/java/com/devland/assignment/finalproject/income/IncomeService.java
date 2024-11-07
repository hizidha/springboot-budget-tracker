package com.devland.assignment.finalproject.income;

import com.devland.assignment.finalproject.applicationuser.ApplicationUserService;
import com.devland.assignment.finalproject.applicationuser.model.ApplicationUser;
import com.devland.assignment.finalproject.financialgoal.FinancialGoalService;
import com.devland.assignment.finalproject.financialgoal.model.FinancialGoal;
import com.devland.assignment.finalproject.income.exception.IncomeNotFoundException;
import com.devland.assignment.finalproject.income.model.Income;
import com.devland.assignment.finalproject.incomecategory.IncomeCategoryService;
import com.devland.assignment.finalproject.incomecategory.model.IncomeCategory;
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
public class IncomeService {
    private final IncomeRepository incomeRepository;
    private final FinancialGoalService financialGoalService;
    private final IncomeCategoryService incomeCategoryService;
    private final ApplicationUserService applicationUserService;
    private final TransactionHistoryService transactionHistoryService;

    public Page<Income> findAll(Optional<String> optionalDescription, String username, Integer month, Integer year, Pageable pageable) {
        ApplicationUser existingUser = this.applicationUserService.findByUsername(username);

        if (month == null || year == null) {
            LocalDate now = LocalDate.now();
            month = (month == null) ? now.getMonthValue() : month;
            year = (year == null) ? now.getYear() : year;
        }

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1);

        if (optionalDescription.isPresent()) {
            return this.incomeRepository.findAllByDescriptionContainsIgnoreCaseAndUserIdAndDateBetween(
                        optionalDescription.get(), existingUser.getId(), startDate, endDate, pageable);
        }

        return this.incomeRepository.findAllByUserIdAndDateBetween(
                    existingUser.getId(), startDate, endDate, pageable);
    }

    public Income findBy(String username, Long id) {
        ApplicationUser existingUser = this.applicationUserService.findByUsername(username);

        return incomeRepository.findByIdAndUserId(id, existingUser.getId())
                .orElseThrow(() -> new IncomeNotFoundException("Income with ID " + id + " not found"));
    }

    public Income create(String username, Income newIncome) {
        ApplicationUser existingUser = this.applicationUserService.findByUsername(username);
        IncomeCategory existingIncomeCategory = this.incomeCategoryService
                .findBy(username, newIncome.getIncomeCategory().getId());

        newIncome.setIncomeCategory(existingIncomeCategory);
        newIncome.setUser(existingUser);

        Income savedIncome = this.incomeRepository.save(newIncome);

        updateTotalBalance(existingUser, savedIncome.getAmount());

        TransactionHistory newTransactionHistory = TransactionHistory.builder()
                .type(TransactionType.INCOME)
                .income(savedIncome)
                .user(existingUser)
                .build();

        TransactionHistory savedTransactionHistory = this.transactionHistoryService
                .create(newTransactionHistory);

        savedIncome.setTransactionHistory(savedTransactionHistory);
        this.incomeRepository.save(savedIncome);

        this.updateFinancialGoals(existingUser);

        return savedIncome;
    }

    public Income update(String username, Income updatedIncome) {
        ApplicationUser existingUser = this.applicationUserService.findByUsername(username);
        Income existingIncome = this.findBy(username, updatedIncome.getId());

        updatedIncome.setId(existingIncome.getId());
        updatedIncome.setIncomeCategory(
                this.incomeCategoryService.findBy(username, updatedIncome.getIncomeCategory().getId())
        );

        BigDecimal amountChange = updatedIncome.getAmount().subtract(existingIncome.getAmount());
        this.updateTotalBalance(existingUser, amountChange);

        TransactionHistory existingTransactionHistory = updatedIncome.getTransactionHistory();
        if (existingTransactionHistory != null) {
            existingTransactionHistory.setIncome(updatedIncome);
            this.transactionHistoryService.update(existingTransactionHistory);
        }

        Income savedIncome = this.incomeRepository.save(updatedIncome);

        this.updateFinancialGoals(existingUser);

        return savedIncome;
    }

    public void delete(String username, Long id) {
        Income existingIncome = this.findBy(username, id);
        ApplicationUser existingUser = this.applicationUserService.findByUsername(username);

        BigDecimal amountChange = existingIncome.getAmount().negate();
        this.updateTotalBalance(existingUser, amountChange);

        this.incomeRepository.delete(existingIncome);

        this.updateFinancialGoals(existingUser);
    }

    private void updateTotalBalance(ApplicationUser user, BigDecimal amountChange) {
        user.setTotalBalance(user.getTotalBalance().add(amountChange));
        this.applicationUserService.save(user);
    }

    private void updateFinancialGoals(ApplicationUser user) {
        List<FinancialGoal> financialGoals = this.financialGoalService.findByUserId(user.getId());

        for (FinancialGoal goals : financialGoals) {
            Double percentage = this.financialGoalService.calculatePercentage(goals, user);
            goals.setPercentage(percentage);

            financialGoalService.save(goals);
        }
    }
}