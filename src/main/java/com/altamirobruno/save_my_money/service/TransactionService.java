package com.altamirobruno.save_my_money.service;

import com.altamirobruno.save_my_money.dto.TransactionDTO;
import com.altamirobruno.save_my_money.dto.TransactionsPageDTO;
import com.altamirobruno.save_my_money.dto.mappers.TransactionMapper;
import com.altamirobruno.save_my_money.exceptions.ItemNotFoundException;
import com.altamirobruno.save_my_money.model.Transaction;
import com.altamirobruno.save_my_money.model.User;
import com.altamirobruno.save_my_money.model.Wallet;
import com.altamirobruno.save_my_money.repository.TransactionRepository;
import com.altamirobruno.save_my_money.repository.WalletRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Validated
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final WalletRepository walletRepository;
    private final UserService userService;

    public TransactionService(TransactionRepository transactionRepository, TransactionMapper transactionMapper, WalletRepository walletRepository, UserService userService) {
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
        this.walletRepository = walletRepository;
        this.userService = userService;
    }


    public TransactionsPageDTO getAll(UUID walletId, Integer month, Integer year, String username, int pageNumber, int pageSize) {
        User user = this.userService.findUserByName(username);
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Transaction> page;

        if (walletId != null && month != null && year != null) {
            page = transactionRepository.findTransactionByWalletIdAndMonthAndYear(walletId, month, year, user.getUserId(), pageable);
        } else if (month != null && year != null) {
            page = transactionRepository.findTransactionByMonthAndYear(month, year, user.getUserId(), pageable);
        } else if (walletId != null) {
            page = transactionRepository.findTransactionByWalletId(walletId, user.getUserId(), pageable);
        } else {
            page = transactionRepository.findTransactionByUserId(user.getUserId(), pageable);
        }

        List<TransactionDTO> transactions = page.get().map(transactionMapper::toDTO).toList();

        return new TransactionsPageDTO(transactions, page.getNumber(), page.getSize(), page.getTotalPages(), page.getTotalPages());


    }

    public TransactionDTO getById(@NotNull UUID id, String username) {
        TransactionDTO transactionDTO = transactionRepository.findById(id).map(transactionMapper::toDTO).orElseThrow(() -> new ItemNotFoundException(id));

        User user = this.userService.findUserByName(username);

        if (!transactionDTO.userId().equals(user.getUserId())) {
            throw new RuntimeException("Access denied: You do not own this transaction");
        }
        return transactionDTO;
    }


    @Transactional
    public TransactionDTO create(@Valid @NotNull TransactionDTO transaction, String username) {
        User user = this.userService.findUserByName(username);
        Transaction transactionEntity = transactionMapper.toEntity(transaction);
        transactionEntity.setUser(user);
        return transactionMapper.toDTO(transactionRepository.save(transactionEntity));
    }

    @Transactional
    public TransactionDTO update(@NotNull UUID id, @Valid @NotNull TransactionDTO transactionDTO, String username) {
        Transaction transactionEntity = transactionMapper.toEntity(getById(id, username));
        transactionEntity.setName(transactionDTO.name());
        transactionEntity.setDescription(transactionDTO.description());
        transactionEntity.setValue(transactionDTO.value());
        transactionEntity.setInstallment(transactionDTO.installment());
        if (transactionDTO.walletId() != null) {
            Wallet wallet = walletRepository.findById(transactionDTO.walletId()).orElseThrow(() -> new ItemNotFoundException(transactionDTO.walletId()));
            transactionEntity.setWallet(wallet);
        }
        return transactionMapper.toDTO(transactionEntity);

    }

    @Transactional
    public void delete(@NotNull UUID id, String username) {
        Transaction transaction = transactionMapper.toEntity(getById(id, username));
        transactionRepository.delete(transaction);
    }
}
