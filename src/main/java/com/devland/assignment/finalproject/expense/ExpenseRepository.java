package com.devland.assignment.finalproject.expense;

import com.devland.assignment.finalproject.expense.model.Expense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    Optional<Expense> findByIdAndUserId(Long id, Long userId);

    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.user.id = :userId AND " +
            "MONTH(e.date) = :month AND YEAR(e.date) = :year")
    BigDecimal sumAmountByUserIdAndMonthAndYear(@Param("userId") Long userId,
                                                @Param("month") Integer month,
                                                @Param("year") Integer year);

    Page<Expense> findAllByDescriptionContainsIgnoreCaseAndUserIdAndDateBetween(String description, Long userId, LocalDate startDate, LocalDate endDate, Pageable pageable);

    Page<Expense> findAllByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate, Pageable pageable);
}