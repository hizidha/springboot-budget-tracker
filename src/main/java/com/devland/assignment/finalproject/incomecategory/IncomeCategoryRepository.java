package com.devland.assignment.finalproject.incomecategory;

import com.devland.assignment.finalproject.incomecategory.model.IncomeCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IncomeCategoryRepository extends JpaRepository<IncomeCategory, Long> {
     Optional<IncomeCategory> findByIdAndUserId(Long id, Long userId);

    Optional<IncomeCategory> findByNameAndUserId(String categoryName, Long userId);

    Page<IncomeCategory> findAllByUserId(Long userId, Pageable pageable);

    Page<IncomeCategory> findAllByNameContainsIgnoreCaseAndUserId(String categoryName, Long userId, Pageable pageable);

    Long countByUserId(Long userId);
}