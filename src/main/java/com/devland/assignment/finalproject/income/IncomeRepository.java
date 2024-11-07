package com.devland.assignment.finalproject.income;

import com.devland.assignment.finalproject.income.model.Income;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface IncomeRepository extends JpaRepository<Income, Long> {
    Optional<Income> findByIdAndUserId(Long id, Long userId);

    Page<Income> findAllByDescriptionContainsIgnoreCaseAndUserIdAndDateBetween(String description, Long userId, LocalDate startDate, LocalDate endDate, Pageable pageable);

    Page<Income> findAllByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate, Pageable pageable);
}