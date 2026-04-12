package com.tammam.roomatedash.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
public class GroceryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(length = 80)
    private String quantity;

    @ManyToOne
    private Roommate assignedTo;

    @ManyToOne
    private Household household;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private GroceryItemStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime purchasedAt;

    public GroceryItem() {
    }

    public GroceryItem(String name, String quantity, Roommate assignedTo) {
        this.name = name;
        this.quantity = quantity;
        this.assignedTo = assignedTo;
        this.status = GroceryItemStatus.NEEDED;
        this.createdAt = LocalDateTime.now();
    }

    public GroceryItem(String name, String quantity, Roommate assignedTo, Household household) {
        this.name = name;
        this.quantity = quantity;
        this.assignedTo = assignedTo;
        this.household = household;
        this.status = GroceryItemStatus.NEEDED;
        this.createdAt = LocalDateTime.now();
    }

    public void markPurchased() {
        this.status = GroceryItemStatus.PURCHASED;
        this.purchasedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getQuantity() {
        return quantity;
    }

    public Roommate getAssignedTo() {
        return assignedTo;
    }

    public Household getHousehold() {
        return household;
    }

    public GroceryItemStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getPurchasedAt() {
        return purchasedAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public void setAssignedTo(Roommate assignedTo) {
        this.assignedTo = assignedTo;
    }

    public void setHousehold(Household household) {
        this.household = household;
    }

    public void setStatus(GroceryItemStatus status) {
        this.status = status;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setPurchasedAt(LocalDateTime purchasedAt) {
        this.purchasedAt = purchasedAt;
    }

    public String getFormattedCreatedAt() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM dd, yyyy - hh:mm a");
        return createdAt.format(fmt);
    }
}
