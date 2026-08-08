package com.altamirobruno.save_my_money.model;

import com.altamirobruno.save_my_money.enums.TransactionType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "tb_transactions")
@ToString
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    @Column(name = "amount_value")
    private BigDecimal value;
    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type = TransactionType.EXPENSE;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;

    private String installment;

    @ManyToOne
    @JoinColumn(name = "wallet_id", columnDefinition = "uuid")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Wallet wallet;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", columnDefinition = "uuid", nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private User user;

    @Column(name = "recurrence_group_id", columnDefinition = "uuid")
    private UUID recurrenceGroupId;

    @PrePersist
    public void prePersist() {
        if (this.date == null)
            this.date = LocalDate.now();
    }
}

