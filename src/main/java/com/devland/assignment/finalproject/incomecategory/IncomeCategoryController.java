package com.devland.assignment.finalproject.incomecategory;

import com.devland.assignment.finalproject.authentication.exception.UnauthorizedException;
import com.devland.assignment.finalproject.authentication.model.AuthenticationService;
import com.devland.assignment.finalproject.incomecategory.model.IncomeCategory;
import com.devland.assignment.finalproject.incomecategory.model.dto.IncomeCategoryRequestDTO;
import com.devland.assignment.finalproject.incomecategory.model.dto.IncomeCategoryResponseDTO;
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
@RequestMapping("/api/users/{username}/income-categories")
public class IncomeCategoryController {
    private final AuthenticationService authenticationService;
    private final IncomeCategoryService incomeCategoryService;

    @GetMapping()
    public ResponseEntity<Page<IncomeCategoryResponseDTO>> getAll(
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

        Page<IncomeCategory> pageCategories = this.incomeCategoryService.findAll(optionalName, username, pageable);
        Page<IncomeCategoryResponseDTO> categoryResponseDTOs = pageCategories.map(IncomeCategory::convertToResponse);

        return ResponseEntity.ok(categoryResponseDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<IncomeCategoryResponseDTO> getOne(
            @PathVariable("username") String username,
            @PathVariable("id") Long id
    ) {
        this.checkCredential(username);

        IncomeCategory existingCategory = this.incomeCategoryService.findBy(username, id);
        IncomeCategoryResponseDTO categoryResponseDTO = existingCategory.convertToResponse();

        return ResponseEntity.ok(categoryResponseDTO);
    }

    @PostMapping()
    public ResponseEntity<IncomeCategoryResponseDTO> create(
            @PathVariable("username") String username,
            @RequestBody @Valid IncomeCategoryRequestDTO categoryRequestDTO
    ) {
        this.checkCredential(username);

        IncomeCategory newCategory = categoryRequestDTO.convertToEntity();
        IncomeCategory savedCategory = this.incomeCategoryService.create(username, newCategory);
        IncomeCategoryResponseDTO categoryResponseDTO = savedCategory.convertToResponse();

        return ResponseEntity.status(HttpStatus.CREATED).body(categoryResponseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<IncomeCategoryResponseDTO> update(
            @PathVariable("username") String username,
            @PathVariable("id") Long id,
            @RequestBody @Valid IncomeCategoryRequestDTO categoryRequestDTO
    ) {
        this.checkCredential(username);

        IncomeCategory updatedCategory = categoryRequestDTO.convertToEntity();
        updatedCategory.setId(id);

        IncomeCategory savedCategory = this.incomeCategoryService.update(username, updatedCategory);
        IncomeCategoryResponseDTO categoryResponseDTO = savedCategory.convertToResponse();

        return ResponseEntity.ok().body(categoryResponseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable("username") String username,
            @PathVariable("id") Long id
    ) {
        this.checkCredential(username);

        this.incomeCategoryService.delete(username, id);

        return ResponseEntity.ok().build();
    }

    private void checkCredential(String username) {
        if (!authenticationService.checkCredential(username)) {
            throw new UnauthorizedException("You are not authorized to access this resource.");
        }
    }
}