package com.tammam.roomatedash.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
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

    private LocalDate dueDate;

    @Column(nullable = false, length = 20)
    private String splitType = "EQUAL";

    @Column(nullable = false)
    private boolean recurring;

    @Column(length = 20)
    private String recurrenceInterval;

    @ManyToOne(optional=false)
    private Roommate payer;

    @ManyToOne
    private Household household;

    @Column(nullable=false)
    private LocalDateTime createdAt;

    public Expense() {}

    public Expense(String description, BigDecimal amount, Roommate payer) {
        this.description = description;
        this.amount = amount;
        this.payer = payer;
        this.createdAt = LocalDateTime.now();
    }

    public Expense(String description, BigDecimal amount, Roommate payer, Household household, LocalDate dueDate, String splitType) {
        this.description = description;
        this.amount = amount;
        this.payer = payer;
        this.household = household;
        this.dueDate = dueDate;
        this.splitType = splitType;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getDescription() { return description; }
    public BigDecimal getAmount() { return amount; }
    public LocalDate getDueDate() { return dueDate; }
    public String getSplitType() { return splitType; }
    public boolean isRecurring() { return recurring; }
    public String getRecurrenceInterval() { return recurrenceInterval; }
    public Roommate getPayer() { return payer; }
    public Household getHousehold() { return household; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setDescription(String description) { this.description = description; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public void setSplitType(String splitType) { this.splitType = splitType; }
    public void setRecurring(boolean recurring) { this.recurring = recurring; }
    public void setRecurrenceInterval(String recurrenceInterval) { this.recurrenceInterval = recurrenceInterval; }
    public void setPayer(Roommate payer) { this.payer = payer; }
    public void setHousehold(Household household) { this.household = household; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getFormattedCreatedAt() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM dd, yyyy - hh:mm a");
        return createdAt.format(fmt);
    }

    public String getFormattedDueDate() {
        return dueDate == null ? "No due date" : dueDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));
    }
}
