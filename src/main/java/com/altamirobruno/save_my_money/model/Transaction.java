package com.altamirobruno.save_my_money.model;

import com.altamirobruno.save_my_money.enums.TransactionType;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "tb_transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    @Column(name = "amount_value")
    private Float value;
    private String description;

    @NotNull
    @Column(nullable = false)
    private TransactionType type = TransactionType.EXPENSE;

    private LocalDate date;
    private String installment;

    @ManyToOne
    @JoinColumn(name = "wallet_id", columnDefinition = "uuid")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Wallet wallet;

    @PrePersist
    public void prePersist() {
        if (this.date == null)
            this.date = LocalDate.now();
    }
}

