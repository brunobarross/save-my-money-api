package com.altamirobruno.save_my_money.controller;

import com.altamirobruno.save_my_money.dto.FinancialSummaryDTO;
import com.altamirobruno.save_my_money.dto.FinancialSummaryProjection;
import com.altamirobruno.save_my_money.dto.TransactionDTO;
import com.altamirobruno.save_my_money.dto.TransactionsPageDTO;
import com.altamirobruno.save_my_money.service.FinancialService;
import com.altamirobruno.save_my_money.service.TransactionService;
import com.altamirobruno.save_my_money.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Validated
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionService transactionService;
    private final FinancialService financialService;


    public TransactionController(TransactionService transactionService, FinancialService financialService, UserService userService) {
        this.transactionService = transactionService;
        this.financialService = financialService;

    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @GetMapping
    public TransactionsPageDTO getAll(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(name = "walletId", required = false) UUID walletId,
            @RequestParam(name = "month", required = false) Integer month,
            @RequestParam(name = "year", required = false) Integer year,
            @RequestParam(name = "page", defaultValue = "0", required = false) @PositiveOrZero int pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "10", required = false) @PositiveOrZero int pageSize
            )

    {

        return transactionService.getAll(walletId, month, year, jwt.getSubject(), pageNumber, pageSize);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @GetMapping("/{id}")
    public TransactionDTO getById(@PathVariable @NotNull UUID id, @AuthenticationPrincipal Jwt jwt) {
        return transactionService.getById(id, jwt.getSubject());
    }


    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @GetMapping("/summary")
    public FinancialSummaryDTO getFinancialSummary(@NotNull int month, @NotNull int year, @AuthenticationPrincipal Jwt jwt) {
        return  financialService.getFinancialSummary(month, year, jwt);
    }

    @PutMapping("/{id}")
    public TransactionDTO update(@PathVariable @NotNull UUID id, @RequestBody TransactionDTO transactionDTO, @AuthenticationPrincipal Jwt jwt) {
        return transactionService.update(id, transactionDTO, jwt.getSubject());

    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public TransactionDTO create(@Valid @RequestBody TransactionDTO transaction, @AuthenticationPrincipal Jwt jwt) {
        return transactionService.create(transaction, jwt.getSubject());
    }


    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable UUID id,
            @RequestParam(name = "scope", defaultValue = "SINGLE", required = false) String scope,
            @AuthenticationPrincipal Jwt jwt) {
        transactionService.delete(id, scope, jwt.getSubject());
    }

    @PostMapping("/{id}/copy")
    @ResponseStatus(code = HttpStatus.CREATED)
    public TransactionDTO copyTransaction(
            @PathVariable("id") UUID transactionID,
            @RequestParam int targetMonth,
            @RequestParam int targetYear,
            @AuthenticationPrincipal Jwt jwt) {
        return transactionService.copyTransaction(transactionID, targetMonth, targetYear, jwt.getSubject());
    }

}
