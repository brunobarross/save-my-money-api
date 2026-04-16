package com.altamirobruno.save_my_money.model;

import com.altamirobruno.save_my_money.enums.TransactionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "Transaction")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String name;
    @Column(name = "amount_value")
    private Float value;
    private String description;

    @NotEmpty
    @NotNull
    @Column(nullable = false)
    private TransactionType type;

    
    private LocalDate date;
    private String installment;

    @ManyToOne
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;

    @PrePersist
    public void prePersist() {
        if (this.date == null)
            this.date = LocalDate.now();
    }
}

