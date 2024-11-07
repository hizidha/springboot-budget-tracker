package com.devland.assignment.finalproject.transactionhistory;

import com.devland.assignment.finalproject.authentication.exception.UnauthorizedException;
import com.devland.assignment.finalproject.authentication.model.AuthenticationService;
import com.devland.assignment.finalproject.income.model.Income;
import com.devland.assignment.finalproject.income.model.dto.IncomeResponseDTO;
import com.devland.assignment.finalproject.transactionhistory.exception.CategoryTypeIsRequiredException;
import com.devland.assignment.finalproject.transactionhistory.model.TransactionHistory;
import com.devland.assignment.finalproject.transactionhistory.model.TransactionType;
import com.devland.assignment.finalproject.transactionhistory.model.dto.TransactionHistoryRequestDTO;
import com.devland.assignment.finalproject.transactionhistory.model.dto.TransactionHistoryResponseDTO;
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

import java.time.LocalDate;
import java.util.Optional;

@SecurityRequirement(name = "bearerAuth")
@RestController
@AllArgsConstructor
@RequestMapping("/users/{username}/transaction-histories")
public class TransactionHistoryController {
    private final AuthenticationService authenticationService;
    private final TransactionHistoryService transactionHistoryService;

    @GetMapping()
    public ResponseEntity<Page<TransactionHistoryResponseDTO>> getAll(
            @PathVariable("username") String username,
            @RequestParam(value = "start_date", required = false) Optional<LocalDate> startDate,
            @RequestParam(value = "end_date", required = false) Optional<LocalDate> endDate,
            @RequestParam(value = "category_type", required = false) Optional<TransactionType> categoryType,
            @RequestParam(value = "category_id", required = false) Optional<Long> categoryId,
            @RequestParam(value = "sort", defaultValue = "ASC") String sortString,
            @RequestParam(value = "order_by", defaultValue = "id") String orderBy,
            @RequestParam(value = "limit", defaultValue = "5") int limit,
            @RequestParam(value = "page", defaultValue = "1") int page
    ) {
        this.checkCredential(username);

        Sort sort = Sort.by(Sort.Direction.valueOf(sortString.toUpperCase()), orderBy);
        Pageable pageable = PageRequest.of(page - 1, limit, sort);

        Page<TransactionHistory> pageTransactions = this.transactionHistoryService
                .findAll(username, startDate, endDate, categoryType, categoryId, pageable);
        Page<TransactionHistoryResponseDTO> transactionResponseDTOs = pageTransactions.map(TransactionHistory::convertToResponse);

        return ResponseEntity.ok(transactionResponseDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionHistoryResponseDTO> getOne(
            @PathVariable("username") String username,
            @PathVariable("id") Long id
    ) {
        this.checkCredential(username);

        TransactionHistory existingTransaction = this.transactionHistoryService.findBy(username, id);
        TransactionHistoryResponseDTO transactionResponseDTO = existingTransaction.convertToResponse();

        return ResponseEntity.ok(transactionResponseDTO);
    }

    private void checkCredential(String username) {
        if (!authenticationService.checkCredential(username)) {
            throw new UnauthorizedException("You are not authorized to access this resource.");
        }
    }
}