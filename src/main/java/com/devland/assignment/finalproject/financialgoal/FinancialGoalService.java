package com.devland.assignment.finalproject.financialgoal;

import com.devland.assignment.finalproject.applicationuser.ApplicationUserService;
import com.devland.assignment.finalproject.applicationuser.model.ApplicationUser;
import com.devland.assignment.finalproject.financialgoal.exception.FinancialGoalAlreadyExist;
import com.devland.assignment.finalproject.financialgoal.exception.FinancialGoalNotFoundException;
import com.devland.assignment.finalproject.financialgoal.model.FinancialGoal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FinancialGoalService {
    private final FinancialGoalRepository financialGoalRepository;
    private final ApplicationUserService applicationUserService;

    public Page<FinancialGoal> findAll(String username, Optional<String> optionalName, Pageable pageable) {
        ApplicationUser user = this.applicationUserService.findByUsername(username);

        if (optionalName.isPresent()) {
            return this.financialGoalRepository.findAllByNameContainsIgnoreCaseAndUserId(
                    optionalName.get(), user.getId(), pageable);
        }

        return this.financialGoalRepository.findAllByUserId(user.getId(), pageable);
    }

    public FinancialGoal findBy(String username, Long id) {
        ApplicationUser user = this.applicationUserService.findByUsername(username);

        return this.financialGoalRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new FinancialGoalNotFoundException("Financial Goal with ID " + id + " not found"));
    }

    public FinancialGoal create(String username, FinancialGoal newFinancialGoal) {
        ApplicationUser existingUser = this.applicationUserService.findByUsername(username);
        checkForExistingGoal(newFinancialGoal, existingUser);

        Double currentPercentage = calculatePercentage(newFinancialGoal, existingUser);
        newFinancialGoal.setPercentage(currentPercentage);
        newFinancialGoal.setUser(existingUser);

        return this.financialGoalRepository.save(newFinancialGoal);
    }

    public FinancialGoal update(String username, FinancialGoal updatedFinancialGoal) {
        FinancialGoal existingFinancialGoal = this.findBy(username, updatedFinancialGoal.getId());
        ApplicationUser existingUser = this.applicationUserService.findByUsername(username);

        Double currentPercentage = calculatePercentage(updatedFinancialGoal, existingUser);

        updatedFinancialGoal.setId(existingFinancialGoal.getId());
        updatedFinancialGoal.setPercentage(currentPercentage);
        updatedFinancialGoal.setUser(existingUser);

        return this.financialGoalRepository.save(updatedFinancialGoal);
    }

    public void delete(String username, Long id) {
        this.financialGoalRepository.deleteByIdAndUserId(id, this.findBy(username, id).getId());
    }

    public List<FinancialGoal> findByUserId(Long userId) {
        return financialGoalRepository.findAllByUserId(userId, Pageable.unpaged()).getContent();
    }

    public void save(FinancialGoal goal) {
        financialGoalRepository.save(goal);
    }

    private void checkForExistingGoal(FinancialGoal newFinancialGoal, ApplicationUser existingUser) {
        Optional<FinancialGoal> existingFinancialGoal = this.financialGoalRepository
                .findByNameAndUserId(newFinancialGoal.getName(), existingUser.getId());

        if (existingFinancialGoal.isPresent()) {
            throw new FinancialGoalAlreadyExist("Financial Goal with Name: " + newFinancialGoal.getName() + " already exists");
        }
    }

    public Double calculatePercentage(FinancialGoal financialGoal, ApplicationUser existingUser) {
        BigDecimal totalBalance = existingUser.getTotalBalance();
        BigDecimal goalAmount = financialGoal.getGoalAmount();

        BigDecimal currentPercentageBigDecimal = BigDecimal.ZERO;
        if (totalBalance.compareTo(BigDecimal.ZERO) != 0) {
            currentPercentageBigDecimal = totalBalance.divide(goalAmount, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }

        if (currentPercentageBigDecimal.compareTo(BigDecimal.valueOf(100)) > 0) {
            currentPercentageBigDecimal = BigDecimal.valueOf(100);
        }

        return currentPercentageBigDecimal.doubleValue();
    }
}