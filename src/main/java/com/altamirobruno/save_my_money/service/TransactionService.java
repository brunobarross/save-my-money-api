package com.altamirobruno.save_my_money.service;

import com.altamirobruno.save_my_money.dto.TransactionDTO;
import com.altamirobruno.save_my_money.dto.mappers.TransactionMapper;
import com.altamirobruno.save_my_money.exceptions.ItemNotFoundException;
import com.altamirobruno.save_my_money.model.Wallet;
import com.altamirobruno.save_my_money.repository.TransactionRepository;
import com.altamirobruno.save_my_money.repository.WalletRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@Validated
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final WalletRepository walletRepository;

    public TransactionService(TransactionRepository transactionRepository, TransactionMapper transactionMapper, WalletRepository walletRepository) {
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
        this.walletRepository = walletRepository;
    }


    public List<TransactionDTO> getAll(UUID walletId, Integer month, Integer year) {
        if (walletId == null) {
            return transactionRepository.findAll()
                    .stream()
                    .map(transactionMapper::toDTO)
                    .toList();
        }
        if(month == null || year == null){
            return transactionRepository.findTransactionByWalletId(walletId)
                    .stream()
                    .map(transactionMapper::toDTO)
                    .toList();
        }

        return transactionRepository.findTransactionByWalletIdAndMonthAndYear(walletId, month, year)
                .stream()
                .map(transactionMapper::toDTO)
                .toList();

    }

    public TransactionDTO getById(@NotNull UUID id) {
        return transactionRepository.findById(id).map(transactionMapper::toDTO).orElseThrow(() -> new ItemNotFoundException(id));
    }

    public TransactionDTO create(@Valid @NotNull TransactionDTO transaction) {
        return transactionMapper.toDTO(transactionRepository.save(transactionMapper.toEntity(transaction)));
    }

    public TransactionDTO update(@NotNull UUID id, @Valid @NotNull TransactionDTO transactionDTO) {
        return transactionRepository.findById(id)
                .map(item -> {
                    item.setName(transactionDTO.name());
                    item.setDescription(transactionDTO.description());
                    item.setValue(transactionDTO.value());
                    item.setInstallment(transactionDTO.installment());
                    if (transactionDTO.wallet() != null && transactionDTO.wallet().id() != null) {
                        Wallet wallet = walletRepository.findById(transactionDTO.wallet().id())
                                .orElseThrow(() -> new ItemNotFoundException(transactionDTO.wallet().id()));
                        item.setWallet(wallet);
                    }
                    return transactionRepository.save(item);
                }).map(transactionMapper::toDTO).orElseThrow(() -> new ItemNotFoundException(id));

    }

    public void delete(@NotNull UUID id) {
        transactionRepository.delete(transactionRepository.findById(id).orElseThrow(() -> new ItemNotFoundException(id)));
    }
}
