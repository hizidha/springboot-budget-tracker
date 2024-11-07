package com.devland.assignment.finalproject.expensecategory;

import com.devland.assignment.finalproject.authentication.exception.UnauthorizedException;
import com.devland.assignment.finalproject.authentication.model.AuthenticationService;
import com.devland.assignment.finalproject.expensecategory.model.ExpenseCategory;
import com.devland.assignment.finalproject.expensecategory.model.dto.ExpenseCategoryRequestDTO;
import com.devland.assignment.finalproject.expensecategory.model.dto.ExpenseCategoryResponseDTO;
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
@RequestMapping("/api/users/{username}/expense-categories")
public class ExpenseCategoryController {
    private final AuthenticationService authenticationService;
    private final ExpenseCategoryService expenseCategoryService;

    @GetMapping()
    public ResponseEntity<Page<ExpenseCategoryResponseDTO>> getAll(
            @PathVariable("username") String username,
            @RequestParam(value = "name") Optional<String> optionalName,
            @RequestParam(value = "sort", defaultValue = "ASC") String sortString,
            @RequestParam(value = "order_by", defaultValue = "id") String orderBy,
            @RequestParam(value = "limit", defaultValue = "5") int limit,
            @RequestParam(value = "page", defaultValue = "1") int page
    ) {
        this.checkCredential(username);

        Sort sort = Sort.by(Sort.Direction.valueOf(sortString.toUpperCase()), orderBy);
        Pageable pageable = PageRequest.of(page - 1, limit, sort);

        Page<ExpenseCategory> pageCategories = this.expenseCategoryService.findAll(optionalName, username, pageable);
        Page<ExpenseCategoryResponseDTO> categoryResponseDTOs = pageCategories.map(ExpenseCategory::convertToResponse);

        return ResponseEntity.ok(categoryResponseDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExpenseCategoryResponseDTO> getOne(
            @PathVariable("username") String username,
            @PathVariable("id") Long id
    ) {
        this.checkCredential(username);

        ExpenseCategory existingCategory = this.expenseCategoryService.findBy(username, id);
        ExpenseCategoryResponseDTO categoryResponseDTO = existingCategory.convertToResponse();

        return ResponseEntity.ok(categoryResponseDTO);
    }

    @PostMapping()
    public ResponseEntity<ExpenseCategoryResponseDTO> create(
            @PathVariable("username") String username,
            @RequestBody @Valid ExpenseCategoryRequestDTO categoryRequestDTO
    ) {
        this.checkCredential(username);

        ExpenseCategory newCategory = categoryRequestDTO.convertToEntity();
        ExpenseCategory savedCategory = this.expenseCategoryService.create(username, newCategory);
        ExpenseCategoryResponseDTO categoryResponseDTO = savedCategory.convertToResponse();

        return ResponseEntity.status(HttpStatus.CREATED).body(categoryResponseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExpenseCategoryResponseDTO> update(
            @PathVariable("username") String username,
            @PathVariable("id") Long id,
            @RequestBody @Valid ExpenseCategoryRequestDTO categoryRequestDTO
    ) {
        this.checkCredential(username);

        ExpenseCategory updatedCategory = categoryRequestDTO.convertToEntity();
        updatedCategory.setId(id);

        ExpenseCategory savedCategory = this.expenseCategoryService.update(username, updatedCategory);
        ExpenseCategoryResponseDTO categoryResponseDTO = savedCategory.convertToResponse();

        return ResponseEntity.ok().body(categoryResponseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable("username") String username,
            @PathVariable("id") Long id
    ) {
        this.checkCredential(username);

        this.expenseCategoryService.delete(username, id);

        return ResponseEntity.ok().build();
    }

    private void checkCredential(String username) {
        if (!authenticationService.checkCredential(username)) {
            throw new UnauthorizedException("You are not authorized to access this resource.");
        }
    }
}