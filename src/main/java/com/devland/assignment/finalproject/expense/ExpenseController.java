package com.devland.assignment.finalproject.expense;

import com.devland.assignment.finalproject.authentication.exception.UnauthorizedException;
import com.devland.assignment.finalproject.authentication.model.AuthenticationService;
import com.devland.assignment.finalproject.expense.model.Expense;
import com.devland.assignment.finalproject.expense.model.dto.ExpenseRequestDTO;
import com.devland.assignment.finalproject.expense.model.dto.ExpenseResponseDTO;
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
@RequestMapping("/api/users/{username}/expenses")
public class ExpenseController {
    private final AuthenticationService authenticationService;
    private final ExpenseService expenseService;

    @GetMapping()
    public ResponseEntity<Page<ExpenseResponseDTO>> getAll(
            @PathVariable("username") String username,
            @RequestParam(value = "search_description") Optional<String> optionalDescription,
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

        Page<Expense> pageExpenses = this.expenseService
                .findAll(optionalDescription, username, month, year, pageable);
        Page<ExpenseResponseDTO> expenseResponseDTOs = pageExpenses.map(Expense::convertToResponse);

        return ResponseEntity.ok(expenseResponseDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExpenseResponseDTO> getOne(
            @PathVariable("username") String username,
            @PathVariable("id") Long id
    ) {
        this.checkCredential(username);

        Expense existingExpense = this.expenseService.findBy(username, id);
        ExpenseResponseDTO expenseResponseDTO = existingExpense.convertToResponse();

        return ResponseEntity.ok(expenseResponseDTO);
    }

    @PostMapping()
    public ResponseEntity<ExpenseResponseDTO> create(
            @PathVariable("username") String username,
            @RequestBody @Valid ExpenseRequestDTO expenseRequestDTO
    ) {
        this.checkCredential(username);

        Expense newExpense = expenseRequestDTO.convertToEntity();
        Expense savedExpense = this.expenseService.create(username, newExpense);
        ExpenseResponseDTO expenseResponseDTO = savedExpense.convertToResponse();

        return ResponseEntity.status(HttpStatus.CREATED).body(expenseResponseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExpenseResponseDTO> update(
            @PathVariable("username") String username,
            @PathVariable("id") Long id,
            @RequestBody @Valid ExpenseRequestDTO expenseRequestDTO
    ) {
        this.checkCredential(username);

        Expense updatedExpense = expenseRequestDTO.convertToEntity();
        updatedExpense.setId(id);

        Expense savedExpense = this.expenseService.update(username, updatedExpense);
        ExpenseResponseDTO categoryResponseDTO = savedExpense.convertToResponse();

        return ResponseEntity.ok().body(categoryResponseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable("username") String username,
            @PathVariable("id") Long id
    ) {
        this.checkCredential(username);

        this.expenseService.delete(username, id);

        return ResponseEntity.ok().build();
    }

    private void checkCredential(String username) {
        if (!authenticationService.checkCredential(username)) {
            throw new UnauthorizedException("You are not authorized to access this resource.");
        }
    }
}