package com.example.demo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity                          // marks this class as a JPA entity → a table
@Table(name = "ledger_entry")    // explicit table name
public class LedgerEntryEntity {

    @Id                          // primary key
    private String id;

    private LocalDate date;
    private String description;

    @Column(precision = 19, scale = 2)   // exact decimal storage for money
    private BigDecimal amount;

    @Column(precision = 19, scale = 2)
    private BigDecimal balance;

    // JPA requires a no-arg constructor
    protected LedgerEntryEntity() {}

    public LedgerEntryEntity(String id, LocalDate date, String description,
                             BigDecimal amount, BigDecimal balance) {
        this.id = id;
        this.date = date;
        this.description = description;
        this.amount = amount;
        this.balance = balance;
    }

    // Getters (JPA + Jackson need these)
    public String getId() { return id; }
    public LocalDate getDate() { return date; }
    public String getDescription() { return description; }
    public BigDecimal getAmount() { return amount; }
    public BigDecimal getBalance() { return balance; }
}