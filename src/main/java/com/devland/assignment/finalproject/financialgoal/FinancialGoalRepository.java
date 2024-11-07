package com.devland.assignment.finalproject.financialgoal;

import com.devland.assignment.finalproject.financialgoal.model.FinancialGoal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FinancialGoalRepository extends JpaRepository<FinancialGoal, Long> {
    Optional<FinancialGoal> findByIdAndUserId(Long id, Long userId);

    Optional<FinancialGoal> findByNameAndUserId(String name, Long userId);

    Page<FinancialGoal> findAllByUserId(Long id, Pageable pageable);

    Page<FinancialGoal> findAllByNameContainsIgnoreCaseAndUserId(String optionalName, Long id, Pageable pageable);

    void deleteByIdAndUserId(Long id, Long userId);
}