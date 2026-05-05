package com.altamirobruno.save_my_money.controller;

import com.altamirobruno.save_my_money.dto.TransactionDTO;
import com.altamirobruno.save_my_money.dto.WalletDTO;
import com.altamirobruno.save_my_money.service.TransactionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionService transactionService;


    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public List<TransactionDTO> getAll() {
        return transactionService.getAll();
    }

    @GetMapping("/{id}")
    public TransactionDTO getById(@PathVariable @NotNull UUID id){
        return transactionService.getById(id);
    }

    @PutMapping("/{id}")
    public TransactionDTO update(@PathVariable @NotNull UUID id, @RequestBody TransactionDTO transactionDTO){
        return  transactionService.update(id, transactionDTO);

    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public TransactionDTO create(@RequestBody @Valid TransactionDTO transaction) {
        return transactionService.create(transaction);
    }


    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        transactionService.delete(id);
    }
}
