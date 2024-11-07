package com.devland.assignment.finalproject.expensecategory;

import com.devland.assignment.finalproject.expensecategory.model.ExpenseCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExpenseCategoryRepository extends JpaRepository<ExpenseCategory, Long> {
    Page<ExpenseCategory> findAllByNameContainsIgnoreCaseAndUserId(String categoryName, Long id, Pageable pageable);

    Page<ExpenseCategory> findAllByUserId(Long userId, Pageable pageable);

    Optional<ExpenseCategory> findByIdAndUserId(Long id, Long userId);

    Optional<ExpenseCategory> findByNameAndUserId(String categoryName, Long id);

    Long countByUserId(Long userId);
}