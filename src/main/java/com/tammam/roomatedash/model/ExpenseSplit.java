package com.tammam.roomatedash.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class ExpenseSplit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false)
    private Expense expense;

    @ManyToOne(optional=false)
    private Roommate roommate;

    @Column(nullable=false, precision=12, scale=2)
    private BigDecimal share;

    @Column(precision=5, scale=2)
    private BigDecimal percentage;

    @Column(nullable = false)
    private boolean paid;

    private LocalDateTime paidAt;

    public ExpenseSplit() {}

    public ExpenseSplit(Expense expense, Roommate roommate, BigDecimal share) {
        this.expense = expense;
        this.roommate = roommate;
        this.share = share;
        this.paid = false;
    }

    public ExpenseSplit(Expense expense, Roommate roommate, BigDecimal share, BigDecimal percentage) {
        this.expense = expense;
        this.roommate = roommate;
        this.share = share;
        this.percentage = percentage;
        this.paid = false;
    }

    public void markPaid() {
        this.paid = true;
        this.paidAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Expense getExpense() { return expense; }
    public Roommate getRoommate() { return roommate; }
    public BigDecimal getShare() { return share; }
    public BigDecimal getPercentage() { return percentage; }
    public boolean isPaid() { return paid; }
    public LocalDateTime getPaidAt() { return paidAt; }

    public void setId(Long id) { this.id = id; }
    public void setExpense(Expense expense) { this.expense = expense; }
    public void setRoommate(Roommate roommate) { this.roommate = roommate; }
    public void setShare(BigDecimal share) { this.share = share; }
    public void setPercentage(BigDecimal percentage) { this.percentage = percentage; }
    public void setPaid(boolean paid) { this.paid = paid; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }
}
