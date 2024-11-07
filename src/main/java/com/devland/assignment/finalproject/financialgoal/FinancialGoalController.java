package com.devland.assignment.finalproject.financialgoal;

import com.devland.assignment.finalproject.authentication.exception.UnauthorizedException;
import com.devland.assignment.finalproject.authentication.model.AuthenticationService;
import com.devland.assignment.finalproject.budget.model.Budget;
import com.devland.assignment.finalproject.budget.model.dto.BudgetRequestDTO;
import com.devland.assignment.finalproject.budget.model.dto.BudgetResponseDTO;
import com.devland.assignment.finalproject.financialgoal.model.FinancialGoal;
import com.devland.assignment.finalproject.financialgoal.model.dto.FinancialGoalRequestDTO;
import com.devland.assignment.finalproject.financialgoal.model.dto.FinancialGoalResponseDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@SecurityRequirement(name = "bearerAuth")
@RestController
@AllArgsConstructor
@RequestMapping("/api/users/{username}/financial-goals")
public class FinancialGoalController {
    private final FinancialGoalService financialGoalService;
    private final AuthenticationService authenticationService;

    @GetMapping()
    public ResponseEntity<Page<FinancialGoalResponseDTO>> getAll(
            @PathVariable("username") String username,
            @RequestParam(value = "name") Optional<String> optionalName,
            @RequestParam(value = "sort", defaultValue = "ASC") String sortString,
            @RequestParam(value = "month", required = false) Integer month,
            @RequestParam(value = "year", required = false) Integer year,
            @RequestParam(value = "order_by", defaultValue = "id") String orderBy,
            @RequestParam(value = "limit", defaultValue = "5") int limit,
            @RequestParam(value = "page", defaultValue = "1") int page
    ) {
        this.checkCredential(username);

        Sort sort = Sort.by(Sort.Direction.valueOf(sortString.toUpperCase()), orderBy);
        Pageable pageable = PageRequest.of(page - 1, limit, sort);

        Page<FinancialGoal> pageFinancialGoals = this.financialGoalService.findAll(username, optionalName, pageable);
        Page<FinancialGoalResponseDTO> financialGoalResponseDTOs = pageFinancialGoals.map(FinancialGoal::convertToResponse);

        return ResponseEntity.ok(financialGoalResponseDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FinancialGoalResponseDTO> getOne(
            @PathVariable("username") String username,
            @PathVariable("id") Long id
    ) {
        this.checkCredential(username);

        FinancialGoal existingFinancialGoal = this.financialGoalService.findBy(username, id);
        FinancialGoalResponseDTO existingFinancialGoalDTO = existingFinancialGoal.convertToResponse();

        return ResponseEntity.ok(existingFinancialGoalDTO);
    }

    @PostMapping()
    public ResponseEntity<FinancialGoalResponseDTO> create(
            @PathVariable("username") String username,
            @RequestBody @Valid FinancialGoalRequestDTO financialGoalRequestDTO
    ) {
        this.checkCredential(username);

        FinancialGoal newFinancialGoal = financialGoalRequestDTO.convertToEntity();
        FinancialGoal savedFinancialGoal = this.financialGoalService.create(username, newFinancialGoal);
        FinancialGoalResponseDTO budgetResponseDTO = savedFinancialGoal.convertToResponse();

        return ResponseEntity.status(HttpStatus.CREATED).body(budgetResponseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FinancialGoalResponseDTO> update(
            @PathVariable("username") String username,
            @PathVariable("id") Long id,
            @RequestBody @Valid FinancialGoalRequestDTO financialGoalRequestDTO
    ) {
        this.checkCredential(username);

        FinancialGoal updatedFinancialGoal = financialGoalRequestDTO.convertToEntity();
        updatedFinancialGoal.setId(id);

        FinancialGoal savedFinancialGoal = this.financialGoalService.update(username, updatedFinancialGoal);
        FinancialGoalResponseDTO financialGoalResponseDTO = savedFinancialGoal.convertToResponse();

        return ResponseEntity.ok().body(financialGoalResponseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable("username") String username,
            @PathVariable("id") Long id
    ) {
        this.checkCredential(username);

        this.financialGoalService.delete(username, id);

        return ResponseEntity.ok().build();
    }

    private void checkCredential(String username) {
        if (!authenticationService.checkCredential(username)) {
            throw new UnauthorizedException("You are not authorized to access this resource.");
        }
    }
}