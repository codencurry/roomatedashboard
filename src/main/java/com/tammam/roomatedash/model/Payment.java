package com.tammam.roomatedash.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Roommate fromRoommate;

    @ManyToOne(optional = false)
    private Roommate toRoommate;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(length = 200)
    private String note;

    @ManyToOne
    private Household household;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public Payment() {
    }

    public Payment(Roommate fromRoommate, Roommate toRoommate, BigDecimal amount, String note) {
        this.fromRoommate = fromRoommate;
        this.toRoommate = toRoommate;
        this.amount = amount;
        this.note = note;
        this.createdAt = LocalDateTime.now();
    }

    public Payment(Roommate fromRoommate, Roommate toRoommate, BigDecimal amount, String note, Household household) {
        this.fromRoommate = fromRoommate;
        this.toRoommate = toRoommate;
        this.amount = amount;
        this.note = note;
        this.household = household;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Roommate getFromRoommate() {
        return fromRoommate;
    }

    public Roommate getToRoommate() {
        return toRoommate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getNote() {
        return note;
    }

    public Household getHousehold() {
        return household;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setFromRoommate(Roommate fromRoommate) {
        this.fromRoommate = fromRoommate;
    }

    public void setToRoommate(Roommate toRoommate) {
        this.toRoommate = toRoommate;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setHousehold(Household household) {
        this.household = household;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getFormattedCreatedAt() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM dd, yyyy - hh:mm a");
        return createdAt.format(fmt);
    }
}
