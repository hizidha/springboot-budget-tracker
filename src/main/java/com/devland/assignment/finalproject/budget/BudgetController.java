package com.devland.assignment.finalproject.budget;

import com.devland.assignment.finalproject.authentication.exception.UnauthorizedException;
import com.devland.assignment.finalproject.authentication.model.AuthenticationService;
import com.devland.assignment.finalproject.budget.model.Budget;
import com.devland.assignment.finalproject.budget.model.dto.BudgetRequestDTO;
import com.devland.assignment.finalproject.budget.model.dto.BudgetResponseDTO;
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

@SecurityRequirement(name = "bearerAuth")
@RestController
@AllArgsConstructor
@RequestMapping("/api/users/{username}/budgets")
public class BudgetController {
    private final BudgetService budgetService;
    private final AuthenticationService authenticationService;

    @GetMapping()
    public ResponseEntity<Page<BudgetResponseDTO>> getAll(
            @PathVariable("username") String username,
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

        Page<Budget> pageBudgets = this.budgetService.findAll(username, month, year, pageable);
        Page<BudgetResponseDTO> budgetResponseDTOs = pageBudgets.map(Budget::convertToResponse);

        return ResponseEntity.ok(budgetResponseDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BudgetResponseDTO> getOne(
            @PathVariable("username") String username,
            @PathVariable("id") Long id
    ) {
        this.checkCredential(username);

        Budget existingBudget = this.budgetService.findBy(username, id);
        BudgetResponseDTO existingBudgetDTO = existingBudget.convertToResponse();

        return ResponseEntity.ok(existingBudgetDTO);
    }

    @PostMapping()
    public ResponseEntity<BudgetResponseDTO> create(
            @PathVariable("username") String username,
            @RequestBody @Valid BudgetRequestDTO budgetRequestDTO
    ) {
        this.checkCredential(username);

        Budget newBudget = budgetRequestDTO.convertToEntity();
        Budget savedBudget = this.budgetService.create(username, newBudget);
        BudgetResponseDTO budgetResponseDTO = savedBudget.convertToResponse();

        return ResponseEntity.status(HttpStatus.CREATED).body(budgetResponseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BudgetResponseDTO> update(
            @PathVariable("username") String username,
            @PathVariable("id") Long id,
            @RequestBody @Valid BudgetRequestDTO budgetRequestDTO
    ) {
        this.checkCredential(username);

        Budget updatedBudget = budgetRequestDTO.convertToEntity();
        updatedBudget.setId(id);

        Budget savedBudget = this.budgetService.update(username, updatedBudget);
        BudgetResponseDTO budgetResponseDTO = savedBudget.convertToResponse();

        return ResponseEntity.ok().body(budgetResponseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable("username") String username,
            @PathVariable("id") Long id
    ) {
        this.checkCredential(username);

        this.budgetService.delete(username, id);

        return ResponseEntity.ok().build();
    }

    private void checkCredential(String username) {
        if (!authenticationService.checkCredential(username)) {
            throw new UnauthorizedException("You are not authorized to access this resource.");
        }
    }
}