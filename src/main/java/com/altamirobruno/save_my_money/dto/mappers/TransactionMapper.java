package com.altamirobruno.save_my_money.dto.mappers;

import com.altamirobruno.save_my_money.dto.TransactionDTO;
import com.altamirobruno.save_my_money.model.Transaction;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class TransactionMapper {
    public TransactionDTO toDTO(Transaction transaction){
        return new TransactionDTO(transaction.getId(), transaction.getName(), transaction.getValue(), transaction.getDescription(), transaction.getDate(), transaction.getInstallment(), transaction.getType());
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

        return transaction;

    }
}
