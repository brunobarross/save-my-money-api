package com.altamirobruno.save_my_money.model;

import com.altamirobruno.save_my_money.enums.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "wallet")
@SQLDelete(sql = "UPDATE wallet SET status = 'DISABLED' WHERE id = ?")
@SQLRestriction("status = 'ACTIVE'")
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotNull
    @Column(length = 30, nullable = false)
    private String name;

    @NotNull
    @NotEmpty
    @Column(name="color", nullable = false)
    private String color;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status = Status.ACTIVE;

    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> transactions = new ArrayList<>();

}
