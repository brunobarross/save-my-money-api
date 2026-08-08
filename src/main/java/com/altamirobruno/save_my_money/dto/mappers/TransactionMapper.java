package com.altamirobruno.save_my_money.dto.mappers;

import com.altamirobruno.save_my_money.dto.TransactionDTO;
import com.altamirobruno.save_my_money.dto.UserDTO;
import com.altamirobruno.save_my_money.dto.WalletDTO;
import com.altamirobruno.save_my_money.exceptions.ItemNotFoundException;
import com.altamirobruno.save_my_money.model.Transaction;
import com.altamirobruno.save_my_money.model.User;
import com.altamirobruno.save_my_money.model.Wallet;
import com.altamirobruno.save_my_money.repository.UserRepository;
import com.altamirobruno.save_my_money.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TransactionMapper {
    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final WalletMapper walletMapper;
    private final UserMapper userMapper;

    public TransactionDTO toDTO(Transaction transaction) {
        WalletDTO walletDTO = transaction.getWallet() == null ? null : walletMapper.toDTO(transaction.getWallet(), transaction.getWallet().getAmount());
        UserDTO userDTO = transaction.getUser() == null ? null : userMapper.toDTO(transaction.getUser());
        if (walletDTO == null || userDTO == null) return null;
        return new TransactionDTO(transaction.getId(), transaction.getName(), transaction.getValue(), transaction.getDescription(), transaction.getDate(), transaction.getInstallment(), transaction.getType(), walletDTO.id(), userDTO.id(), transaction.getRecurrenceGroupId(), null);
    }

    public Transaction toEntity(TransactionDTO transactionDTO) {
        if (transactionDTO == null) {
            return null;
        }

        Transaction transaction = new Transaction();

        if (transactionDTO.id() != null) {
            transaction.setId(transactionDTO.id());
        }
        transaction.setName(transactionDTO.name());
        transaction.setDate(transactionDTO.date());
        transaction.setInstallment(transactionDTO.installment());
        transaction.setDescription(transactionDTO.description());
        transaction.setType(transactionDTO.type());
        transaction.setValue(transactionDTO.value());
        if (transactionDTO.walletId() != null) {
            Wallet wallet = walletRepository.findById(transactionDTO.walletId())
                    .orElseThrow(() -> new ItemNotFoundException(transactionDTO.walletId()));
            transaction.setWallet(wallet);
        }
        if (transactionDTO.userId() != null) {
            User user = userRepository.findById(transactionDTO.userId())
                    .orElseThrow(() -> new ItemNotFoundException(transactionDTO.userId()));
            transaction.setUser(user);
        }
        return transaction;
    }
}
