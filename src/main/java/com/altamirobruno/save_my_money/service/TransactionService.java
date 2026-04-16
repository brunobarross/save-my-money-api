package com.altamirobruno.save_my_money.service;

import com.altamirobruno.save_my_money.dto.TransactionDTO;
import com.altamirobruno.save_my_money.dto.WalletDTO;
import com.altamirobruno.save_my_money.dto.mappers.TransactionMapper;
import com.altamirobruno.save_my_money.exceptions.ItemNotFoundException;
import com.altamirobruno.save_my_money.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    public TransactionService(TransactionRepository transactionRepository, TransactionMapper transactionMapper) {
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
    }


    public List<TransactionDTO> getAll() {
        return transactionRepository.findAll()
                .stream()
                .map(transactionMapper::toDTO)
                .collect(Collectors.toList());
    }

    public TransactionDTO create(TransactionDTO transaction) {
        return transactionMapper.toDTO(transactionRepository.save(transactionMapper.toEntity(transaction)));
    }

    public void delete(Long id) {
        transactionRepository.delete(transactionRepository.findById(id).orElseThrow(() -> new ItemNotFoundException(id)));
    }
}
