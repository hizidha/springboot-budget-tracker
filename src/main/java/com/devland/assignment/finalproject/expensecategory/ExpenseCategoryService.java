package com.devland.assignment.finalproject.expensecategory;

import com.devland.assignment.finalproject.applicationuser.ApplicationUserService;
import com.devland.assignment.finalproject.applicationuser.model.ApplicationUser;
import com.devland.assignment.finalproject.expensecategory.exception.CannotDeleteLastExpenseCategoryException;
import com.devland.assignment.finalproject.expensecategory.exception.ExpenseCategoryAlreadyExistException;
import com.devland.assignment.finalproject.expensecategory.exception.ExpenseCategoryNotFoundException;
import com.devland.assignment.finalproject.expensecategory.model.ExpenseCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExpenseCategoryService {
    private final ExpenseCategoryRepository expenseCategoryRepository;
    private final ApplicationUserService applicationUserService;

    public Page<ExpenseCategory> findAll(Optional<String> optionalName, String username, Pageable pageable) {
        ApplicationUser existingUser = this.applicationUserService.findByUsername(username);

        if (optionalName.isPresent()) {
            return this.expenseCategoryRepository.findAllByNameContainsIgnoreCaseAndUserId(optionalName.get(), existingUser.getId(), pageable);
        }
        return this.expenseCategoryRepository.findAllByUserId(existingUser.getId(), pageable);
    }

    public ExpenseCategory findBy(String username, Long id) {
        ApplicationUser existingUser = this.applicationUserService.findByUsername(username);

        return expenseCategoryRepository.findByIdAndUserId(id, existingUser.getId())
                .orElseThrow(() -> new ExpenseCategoryNotFoundException("Income Category with ID " + id + " not found"));
    }

    public ExpenseCategory create(String username, ExpenseCategory newCategory) {
        ApplicationUser existingUser = this.applicationUserService.findByUsername(username);

        Optional<ExpenseCategory> existingCategory = this.expenseCategoryRepository
                .findByNameAndUserId(newCategory.getName(), existingUser.getId());

        if (existingCategory.isPresent()) {
            throw new ExpenseCategoryAlreadyExistException("Category: " + newCategory.getName() + " already exist");
        }

        newCategory.setUser(existingUser);

        return this.expenseCategoryRepository.save(newCategory);
    }

    public ExpenseCategory update(String username, ExpenseCategory updatedCategory) {
        ExpenseCategory existingCategory = this.findBy(username, updatedCategory.getId());

        if (!existingCategory.getName().equals(updatedCategory.getName())) {
            Optional<ExpenseCategory> existingCategoryWithName = this.expenseCategoryRepository
                    .findByNameAndUserId(updatedCategory.getName(), existingCategory.getUser().getId());

            if (existingCategoryWithName.isPresent()) {
                throw new ExpenseCategoryAlreadyExistException("Category: " + updatedCategory.getName() + " already exists.");
            }
        }

        updatedCategory.setId(existingCategory.getId());
        updatedCategory.setUser(existingCategory.getUser());

        return this.expenseCategoryRepository.save(updatedCategory);
    }

    public void delete(String username, Long id) {
        ApplicationUser existingUser = this.applicationUserService.findByUsername(username);

        Long count = expenseCategoryRepository.countByUserId(existingUser.getId());
        if (count <= 1) {
            throw new CannotDeleteLastExpenseCategoryException("Cannot delete the last Expense Category. At least one Expense Category must remain.");
        }

        this.expenseCategoryRepository.deleteById(this.findBy(username, id).getId());
    }

    public void createDefaultCategories(ApplicationUser user) {
        List<String> defaultExpenseCategories = Arrays.asList("Food & Drinks", "Rent", "Transportation", "Telephone Credit", "Medical", "Others");

        for (String categoryName : defaultExpenseCategories) {
            ExpenseCategory expenseCategory = new ExpenseCategory();
            expenseCategory.setName(categoryName);
            expenseCategory.setUser(user);
            expenseCategoryRepository.save(expenseCategory);
        }
    }
}