package com.devland.assignment.finalproject.transactionhistory;

import com.devland.assignment.finalproject.transactionhistory.model.TransactionHistory;
import com.devland.assignment.finalproject.transactionhistory.model.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, Long> {
    Optional<TransactionHistory> findByIdAndUserId(Long id, Long userId);

    @Query("SELECT t FROM TransactionHistory t " +
            "LEFT JOIN t.income i ON t.type = 'INCOME' " +
            "LEFT JOIN t.expense e ON t.type = 'EXPENSE' " +
            "WHERE t.user.username = :username " +
            "AND ((t.type = 'INCOME' AND i.date BETWEEN :start AND :end) " +
            "OR (t.type = 'EXPENSE' AND e.date BETWEEN :start AND :end))")
    Page<TransactionHistory> findByIncomeOrExpenseDate(@Param("username") String username,
                                                       @Param("start") LocalDate start,
                                                       @Param("end") LocalDate end,
                                                       Pageable pageable);

    @Query("SELECT t FROM TransactionHistory t " +
            "LEFT JOIN t.income i ON t.type = 'INCOME' " +
            "LEFT JOIN t.expense e ON t.type = 'EXPENSE' " +
            "WHERE t.user.username = :username " +
            "AND ((t.type = 'INCOME' AND t.type = :type AND i.incomeCategory.id = :categoryId AND i.date BETWEEN :start AND :end) " +
            "OR (t.type = 'EXPENSE' AND t.type = :type AND e.expenseCategory.id = :categoryId AND e.date BETWEEN :start AND :end))")
    Page<TransactionHistory> findByTypeAndCategory(@Param("username") String username,
                                                   @Param("start") LocalDate start,
                                                   @Param("end") LocalDate end,
                                                   @Param("type") TransactionType type,
                                                   @Param("categoryId") Long categoryId,
                                                   Pageable pageable);
}