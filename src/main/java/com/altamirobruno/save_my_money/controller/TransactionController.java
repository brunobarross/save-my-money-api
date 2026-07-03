package com.altamirobruno.save_my_money.controller;

import com.altamirobruno.save_my_money.dto.FinancialSummaryDTO;
import com.altamirobruno.save_my_money.dto.FinancialSummaryProjection;
import com.altamirobruno.save_my_money.dto.TransactionDTO;
import com.altamirobruno.save_my_money.service.FinancialService;
import com.altamirobruno.save_my_money.service.TransactionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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


    public TransactionController(TransactionService transactionService, FinancialService financialService) {
        this.transactionService = transactionService;
        this.financialService = financialService;
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @GetMapping
    public List<TransactionDTO> getAll(
            @RequestParam(name = "walletId", required = false) UUID walletId,
            @RequestParam(name = "month", required = false) Integer month,
            @RequestParam(name = "year", required = false) Integer year) {

        return transactionService.getAll(walletId, month, year);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @GetMapping("/{id}")
    public TransactionDTO getById(@PathVariable @NotNull UUID id) {
        return transactionService.getById(id);
    }


    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @GetMapping("/summary")
    public FinancialSummaryDTO getFinancialSummary(@NotNull int month, @NotNull int year, @AuthenticationPrincipal Jwt jwt) {
        return  financialService.getFinancialSummary(month, year, jwt);
    }

    @PutMapping("/{id}")
    public TransactionDTO update(@PathVariable @NotNull UUID id, @RequestBody TransactionDTO transactionDTO) {
        return transactionService.update(id, transactionDTO);

    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public TransactionDTO create(@Valid @RequestBody TransactionDTO transaction) {
        return transactionService.create(transaction);
    }


    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        transactionService.delete(id);
    }
}
