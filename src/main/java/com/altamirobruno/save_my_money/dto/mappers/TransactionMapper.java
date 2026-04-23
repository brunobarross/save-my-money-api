package com.altamirobruno.save_my_money.dto.mappers;

import com.altamirobruno.save_my_money.dto.TransactionDTO;
import com.altamirobruno.save_my_money.dto.WalletDTO;
import com.altamirobruno.save_my_money.exceptions.ItemNotFoundException;
import com.altamirobruno.save_my_money.model.Transaction;
import com.altamirobruno.save_my_money.model.Wallet;
import com.altamirobruno.save_my_money.repository.WalletRepository;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
public class TransactionMapper {
    private final WalletRepository walletRepository;
    public TransactionDTO toDTO(Transaction transaction){

        return new TransactionDTO(transaction.getId(), transaction.getName(), transaction.getValue(), transaction.getDescription(), transaction.getDate(), transaction.getInstallment(), transaction.getType(), transaction.getWallet());
    }

    public Transaction toEntity(TransactionDTO transactionDTO){
        if(transactionDTO == null) {
            return null;
        }

        Transaction transaction = new Transaction();

        if(transactionDTO.id() != null){
            transaction.setId(transactionDTO.id());
        }
        transaction.setName(transactionDTO.name());
        transaction.setDate(transactionDTO.date());
        transaction.setInstallment(transactionDTO.installment());
        transaction.setDescription(transactionDTO.description());
        transaction.setType(transactionDTO.type());
        transaction.setValue(transactionDTO.value());
        if (transactionDTO.wallet() != null && transactionDTO.wallet().getId() != null) {
            Wallet wallet = walletRepository.findById(transactionDTO.wallet().getId())
                    .orElseThrow(() -> new ItemNotFoundException(transactionDTO.wallet().getId()));
            transaction.setWallet(wallet);
        }
        return transaction;

    }
}
