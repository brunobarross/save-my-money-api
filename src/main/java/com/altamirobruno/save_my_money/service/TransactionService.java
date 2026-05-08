package com.altamirobruno.save_my_money.service;

import com.altamirobruno.save_my_money.dto.TransactionDTO;
import com.altamirobruno.save_my_money.dto.WalletDTO;
import com.altamirobruno.save_my_money.dto.mappers.TransactionMapper;
import com.altamirobruno.save_my_money.exceptions.ItemNotFoundException;
import com.altamirobruno.save_my_money.model.Wallet;
import com.altamirobruno.save_my_money.repository.TransactionRepository;
import com.altamirobruno.save_my_money.repository.WalletRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    public TransactionService(TransactionRepository transactionRepository, TransactionMapper transactionMapper, WalletRepository walletRepository) {
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
    }


    public List<TransactionDTO> getAll() {
        return transactionRepository.findAll()
                .stream()
                .map(transactionMapper::toDTO)
                .collect(Collectors.toList());
    }

    public TransactionDTO getById(@NotNull UUID id){
        return transactionRepository.findById(id).map(transactionMapper::toDTO).orElseThrow(()-> new ItemNotFoundException(id));
    }

    public TransactionDTO create(@Valid @NotNull TransactionDTO transaction) {
        return transactionMapper.toDTO(transactionRepository.save(transactionMapper.toEntity(transaction)));
    }

    public TransactionDTO update(@NotNull UUID id, @Valid @NotNull TransactionDTO transactionDTO){
        return transactionRepository.findById(id)
                .map(item ->{
                   item.setName(transactionDTO.name());
                   item.setDescription(transactionDTO.description());
                   item.setValue(transactionDTO.value());
                   item.setInstallment(transactionDTO.installment());
                   item.setWallet(transactionDTO.wallet());
                   return transactionRepository.save(item);
                }).map(transactionMapper::toDTO).orElseThrow(()-> new ItemNotFoundException(id));

    }

    public void delete(@NotNull UUID id) {
        transactionRepository.delete(transactionRepository.findById(id).orElseThrow(() -> new ItemNotFoundException(id)));
    }
}
