package com.devland.assignment.finalproject.budget;

import com.devland.assignment.finalproject.budget.model.Budget;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    Optional<Budget> findByIdAndUserId(Long id, Long userId);

    Optional<Budget> findByUserIdAndCategoryIdAndMonthAndYear(Long userId, Long categoryId, Integer month, Integer year);

    Optional<Budget> findByCategoryIdAndUserId(Long id, Long userId);

    Page<Budget> findAllByUserIdAndMonthAndYear(Long id, Integer month, Integer year, Pageable pageable);

    boolean existsByUserIdAndCategoryIdAndMonthAndYear(Long userId, Long categoryId, Integer month, Integer year);
}