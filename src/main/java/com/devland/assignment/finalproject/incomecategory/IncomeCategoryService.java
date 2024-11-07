package com.devland.assignment.finalproject.incomecategory;

import com.devland.assignment.finalproject.applicationuser.ApplicationUserService;
import com.devland.assignment.finalproject.applicationuser.model.ApplicationUser;
import com.devland.assignment.finalproject.incomecategory.exception.CannotDeleteLastIncomeCategoryException;
import com.devland.assignment.finalproject.incomecategory.exception.IncomeCategoryAlreadyExistException;
import com.devland.assignment.finalproject.incomecategory.exception.IncomeCategoryNotFoundException;
import com.devland.assignment.finalproject.incomecategory.model.IncomeCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IncomeCategoryService {
    private final ApplicationUserService applicationUserService;
    private final IncomeCategoryRepository incomeCategoryRepository;

    public Page<IncomeCategory> findAll(Optional<String> optionalName, String username, Pageable pageable) {
        ApplicationUser existingUser = this.applicationUserService.findByUsername(username);

        if (optionalName.isPresent()) {
            return this.incomeCategoryRepository.findAllByNameContainsIgnoreCaseAndUserId(optionalName.get(), existingUser.getId(), pageable);
        }
        return this.incomeCategoryRepository.findAllByUserId(existingUser.getId(), pageable);
    }

    public IncomeCategory findBy(String username, Long id) {
        ApplicationUser existingUser = this.applicationUserService.findByUsername(username);

        return incomeCategoryRepository.findByIdAndUserId(id, existingUser.getId())
                .orElseThrow(() -> new IncomeCategoryNotFoundException("Income Category with ID " + id + " not found"));
    }

    public IncomeCategory create(String username, IncomeCategory newCategory) {
        ApplicationUser existingUser = this.applicationUserService.findByUsername(username);

        Optional<IncomeCategory> existingCategory = this.incomeCategoryRepository
                .findByNameAndUserId(newCategory.getName(), existingUser.getId());

        if (existingCategory.isPresent()) {
            throw new IncomeCategoryAlreadyExistException("Category: " + newCategory.getName() + " already exist");
        }

        newCategory.setUser(existingUser);

        return this.incomeCategoryRepository.save(newCategory);
    }

    public IncomeCategory update(String username, IncomeCategory updatedCategory) {
        IncomeCategory existingCategory = this.findBy(username, updatedCategory.getId());

        if (!existingCategory.getName().equals(updatedCategory.getName())) {
            Optional<IncomeCategory> existingCategoryWithName = this.incomeCategoryRepository
                    .findByNameAndUserId(updatedCategory.getName(), existingCategory.getUser().getId());

            if (existingCategoryWithName.isPresent()) {
                throw new IncomeCategoryAlreadyExistException("Category: " + updatedCategory.getName() + " already exists.");
            }
        }

        updatedCategory.setId(existingCategory.getId());
        updatedCategory.setUser(existingCategory.getUser());

        return this.incomeCategoryRepository.save(updatedCategory);
    }

    public void delete(String username, Long id) {
        ApplicationUser existingUser = this.applicationUserService.findByUsername(username);

        Long count = incomeCategoryRepository.countByUserId(existingUser.getId());
        if (count <= 1) {
            throw new CannotDeleteLastIncomeCategoryException("Cannot delete the last Income Category. At least one Income Category must remain.");
        }

        this.incomeCategoryRepository.deleteById(this.findBy(username, id).getId());
    }

    public void createDefaultCategories(ApplicationUser user) {
        List<String> defaultIncomeCategories = Arrays.asList("Salary", "Extra Income", "Others");

        for (String categoryName : defaultIncomeCategories) {
            IncomeCategory incomeCategory = new IncomeCategory();
            incomeCategory.setName(categoryName);
            incomeCategory.setUser(user);
            incomeCategoryRepository.save(incomeCategory);
        }
    }
}