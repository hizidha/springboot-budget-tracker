package com.devland.assignment.finalproject.income;

import com.devland.assignment.finalproject.authentication.exception.UnauthorizedException;
import com.devland.assignment.finalproject.authentication.model.AuthenticationService;
import com.devland.assignment.finalproject.income.model.Income;
import com.devland.assignment.finalproject.income.model.dto.IncomeRequestDTO;
import com.devland.assignment.finalproject.income.model.dto.IncomeResponseDTO;
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
@RequestMapping("/api/users/{username}/incomes")
public class IncomeController {
    private final AuthenticationService authenticationService;
    private final IncomeService incomeService;

    @GetMapping()
    public ResponseEntity<Page<IncomeResponseDTO>> getAll(
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

        Page<Income> pageIncomes = this.incomeService
                .findAll(optionalDescription, username, month, year, pageable);
        Page<IncomeResponseDTO> incomeResponseDTOs = pageIncomes.map(Income::convertToResponse);

        return ResponseEntity.ok(incomeResponseDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<IncomeResponseDTO> getOne(
            @PathVariable("username") String username,
            @PathVariable("id") Long id
    ) {
        this.checkCredential(username);

        Income existingIncome = this.incomeService.findBy(username, id);
        IncomeResponseDTO incomeResponseDTO = existingIncome.convertToResponse();

        return ResponseEntity.ok(incomeResponseDTO);
    }

    @PostMapping()
    public ResponseEntity<IncomeResponseDTO> create(
            @PathVariable("username") String username,
            @RequestBody @Valid IncomeRequestDTO incomeRequestDTO
    ) {
        this.checkCredential(username);

        Income newIncome = incomeRequestDTO.convertToEntity();
        Income savedIncome = this.incomeService.create(username, newIncome);
        IncomeResponseDTO incomeResponseDTO = savedIncome.convertToResponse();

        return ResponseEntity.status(HttpStatus.CREATED).body(incomeResponseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<IncomeResponseDTO> update(
            @PathVariable("username") String username,
            @PathVariable("id") Long id,
            @RequestBody @Valid IncomeRequestDTO incomeRequestDTO
    ) {
        this.checkCredential(username);

        Income updatedIncome = incomeRequestDTO.convertToEntity();
        updatedIncome.setId(id);

        Income savedIncome = this.incomeService.update(username, updatedIncome);
        IncomeResponseDTO incomeResponseDTO = savedIncome.convertToResponse();

        return ResponseEntity.ok().body(incomeResponseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable("username") String username,
            @PathVariable("id") Long id
    ) {
        this.checkCredential(username);

        this.incomeService.delete(username, id);

        return ResponseEntity.ok().build();
    }

    private void checkCredential(String username) {
        if (!authenticationService.checkCredential(username)) {
            throw new UnauthorizedException("You are not authorized to access this resource.");
        }
    }
}