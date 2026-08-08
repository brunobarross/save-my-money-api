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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
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

    private Wallet getWalletById(UUID walletId) {
        if (walletId == null) {
            return null;
        }
        return walletRepository.findById(walletId)
                .orElseThrow(() -> new ItemNotFoundException(walletId));
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
        Transaction transaction = getTransactionEntityAndCheckOwnership(id, username);
        return transactionMapper.toDTO(transaction);
    }


    @Transactional
    public TransactionDTO create(@Valid @NotNull TransactionDTO transactionDTO, String username) {
        User user = this.userService.findUserByName(username);

        if(transactionDTO.installmentCount() == null || transactionDTO.installmentCount() == 0) {
            Transaction transactionEntity = transactionMapper.toEntity(transactionDTO);
            transactionEntity.setUser(user);
            return transactionMapper.toDTO(transactionRepository.save(transactionEntity));
        }

        List<Transaction> installments = this.createInstallments(transactionDTO, user, transactionDTO.installmentCount());

        List<Transaction> savedTransactions = transactionRepository.saveAll(installments);
        return transactionMapper.toDTO(savedTransactions.getFirst());

    }

    @Transactional
    public TransactionDTO update(@NotNull UUID id, @Valid @NotNull TransactionDTO transactionDTO, String username) {
        Transaction transactionEntity = getTransactionEntityAndCheckOwnership(id, username);
        transactionEntity.setName(transactionDTO.name());
        transactionEntity.setDescription(transactionDTO.description());
        transactionEntity.setValue(transactionDTO.value());
        transactionEntity.setInstallment(transactionDTO.installment());
        transactionEntity.setWallet(getWalletById(transactionDTO.walletId()));
        return transactionMapper.toDTO(transactionEntity);

    }

    @Transactional
    public void delete(@NotNull UUID id, String username) {
        Transaction transaction = getTransactionEntityAndCheckOwnership(id, username);
        transactionRepository.delete(transaction);
    }


    private List<Transaction> createInstallments(TransactionDTO transactionDTO, User user, int totalInstallments) {
        LocalDate baseDate = transactionDTO.date() == null ? LocalDate.now() : transactionDTO.date();
        UUID groupId = UUID.randomUUID();

        List<Transaction> transactionsList = new ArrayList<>();
        Wallet wallet = getWalletById(transactionDTO.walletId());

        for(int i = 0; i < totalInstallments; i++) {
            Transaction transaction = new Transaction();
            transaction.setName(transactionDTO.name());
            transaction.setDescription(transactionDTO.description());
            transaction.setValue(transactionDTO.value());
            transaction.setType(transactionDTO.type());
            transaction.setRecurrenceGroupId(groupId);
            transaction.setDate(baseDate.plusMonths(i));
            transaction.setUser(user);
            transaction.setInstallment((i + 1) + "/" + totalInstallments);
            transaction.setWallet(wallet);
            transactionsList.add(transaction);
        }
        return transactionsList;
    }

    @Transactional
    public TransactionDTO copyTransaction(UUID transactionID, int targetMonth, int targetYear, String username){
        Transaction originTransaction = getTransactionEntityAndCheckOwnership(transactionID, username);
        LocalDate originDate = originTransaction.getDate() != null ? originTransaction.getDate() : LocalDate.now();
        LocalDate targetDate = originDate.withMonth(targetMonth).withYear(targetYear);

        Transaction targetTransaction = new Transaction();
        targetTransaction.setName(originTransaction.getName());
        targetTransaction.setDescription(originTransaction.getDescription());
        targetTransaction.setValue(originTransaction.getValue());
        targetTransaction.setType(originTransaction.getType());
        targetTransaction.setRecurrenceGroupId(originTransaction.getRecurrenceGroupId());
        targetTransaction.setDate(targetDate);
        if (originTransaction.getWallet() != null) {
            targetTransaction.setWallet(getWalletById(originTransaction.getWallet().getId()));
        }
        targetTransaction.setInstallment(originTransaction.getInstallment());
        targetTransaction.setUser(originTransaction.getUser());
        return transactionMapper.toDTO(transactionRepository.save(targetTransaction));

    }

    private Transaction getTransactionEntityAndCheckOwnership(UUID id, String username) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(id));

        User user = this.userService.findUserByName(username);

        if (!transaction.getUser().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("Access denied: You do not own this transaction");
        }

        return transaction;
    }
}
