package com.altamirobruno.save_my_money.model;

import jakarta.persistence.*;
import lombok.Data;

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

}
