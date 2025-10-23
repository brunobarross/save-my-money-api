package com.altamirobruno.save_my_money.model;

import com.altamirobruno.save_my_money.enums.Status;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.processing.Pattern;

@Data
@Entity
@Table
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name="balance", nullable = false)
    private Double balance;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status", nullable = false)
    private Status status = Status.ACTIVE;

}
