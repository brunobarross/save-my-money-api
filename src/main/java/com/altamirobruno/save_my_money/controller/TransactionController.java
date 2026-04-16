package com.altamirobruno.save_my_money.controller;

import com.altamirobruno.save_my_money.dto.TransactionDTO;
import com.altamirobruno.save_my_money.dto.WalletDTO;
import com.altamirobruno.save_my_money.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping
    public TransactionDTO create(@RequestBody TransactionDTO transaction) {
        return transactionService.create(transaction);
    }

    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        transactionService.delete(id);
    }
}
