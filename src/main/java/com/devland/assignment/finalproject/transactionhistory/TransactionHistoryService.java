package com.devland.assignment.finalproject.transactionhistory;

import com.devland.assignment.finalproject.applicationuser.ApplicationUserService;
import com.devland.assignment.finalproject.applicationuser.model.ApplicationUser;
import com.devland.assignment.finalproject.income.exception.IncomeNotFoundException;
import com.devland.assignment.finalproject.transactionhistory.exception.CategoryTypeIsRequiredException;
import com.devland.assignment.finalproject.transactionhistory.exception.TransactionHistoryNotFoundException;
import com.devland.assignment.finalproject.transactionhistory.model.TransactionHistory;
import com.devland.assignment.finalproject.transactionhistory.model.TransactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
public class TransactionHistoryService {
    private final TransactionHistoryRepository transactionHistoryRepository;
    private final ApplicationUserService applicationUserService;

    public TransactionHistory create(TransactionHistory newTransactionHistory) {
        return this.transactionHistoryRepository.save(newTransactionHistory);
    }

    public void update(TransactionHistory existingTransactionHistory) {
         existingTransactionHistory = transactionHistoryRepository.findById(existingTransactionHistory.getId())
                 .orElseThrow(() -> new TransactionHistoryNotFoundException("Transaction History not found"));

        transactionHistoryRepository.save(existingTransactionHistory);
    }

    public Page<TransactionHistory> findAll(String username, Optional<LocalDate> startDate, Optional<LocalDate> endDate,
                                            Optional<TransactionType> transactionType, Optional<Long> categoryId, Pageable pageable
    ) {
        LocalDate start = startDate.orElse(LocalDate.now().withDayOfMonth(1));
        LocalDate end = endDate.orElse(LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()));

        if (categoryId.isPresent() && transactionType.isEmpty()) {
            throw new CategoryTypeIsRequiredException("Category Type must be specified when Category Id is provided.");
        }

        return categoryId.isPresent()
                ? transactionHistoryRepository.findByTypeAndCategory(username, start, end, transactionType.get(), categoryId.get(), pageable)
                : transactionHistoryRepository.findByIncomeOrExpenseDate(username, start, end, pageable);
    }

    public TransactionHistory findBy(String username, Long id) {
        ApplicationUser existingUser = this.applicationUserService.findByUsername(username);

        return transactionHistoryRepository.findByIdAndUserId(id, existingUser.getId())
                .orElseThrow(() -> new IncomeNotFoundException("Income with ID " + id + " not found"));
    }
}