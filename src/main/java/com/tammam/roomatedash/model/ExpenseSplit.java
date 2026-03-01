package com.tammam.roomatedash.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

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

    public ExpenseSplit() {}

    public ExpenseSplit(Expense expense, Roommate roommate, BigDecimal share) {
        this.expense = expense;
        this.roommate = roommate;
        this.share = share;
    }

    public Long getId() { return id; }
    public Expense getExpense() { return expense; }
    public Roommate getRoommate() { return roommate; }
    public BigDecimal getShare() { return share; }

    public void setId(Long id) { this.id = id; }
    public void setExpense(Expense expense) { this.expense = expense; }
    public void setRoommate(Roommate roommate) { this.roommate = roommate; }
    public void setShare(BigDecimal share) { this.share = share; }
}
