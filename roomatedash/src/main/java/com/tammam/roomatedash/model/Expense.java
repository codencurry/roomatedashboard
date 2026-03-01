package com.tammam.roomatedash.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String description;

    @Column(nullable=false, precision=12, scale=2)
    private BigDecimal amount;

    @ManyToOne(optional=false)
    private Roommate payer;

    @Column(nullable=false)
    private LocalDateTime createdAt;

    public Expense() {}

    public Expense(String description, BigDecimal amount, Roommate payer) {
        this.description = description;
        this.amount = amount;
        this.payer = payer;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getDescription() { return description; }
    public BigDecimal getAmount() { return amount; }
    public Roommate getPayer() { return payer; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setDescription(String description) { this.description = description; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setPayer(Roommate payer) { this.payer = payer; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getFormattedCreatedAt() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM dd, yyyy - hh:mm a");
        return createdAt.format(fmt);
    }
}
