package com.tammam.roomatedash.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
public class Chore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(length = 300)
    private String description;

    @ManyToOne(optional = false)
    private Roommate assignedTo;

    @ManyToOne
    private Household household;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ChoreStatus status;

    private LocalDate dueDate;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime completedAt;

    @Column(nullable = false)
    private boolean rotating;

    public Chore() {
    }

    public Chore(String title, String description, Roommate assignedTo, LocalDate dueDate) {
        this.title = title;
        this.description = description;
        this.assignedTo = assignedTo;
        this.dueDate = dueDate;
        this.status = ChoreStatus.OPEN;
        this.createdAt = LocalDateTime.now();
    }

    public Chore(String title, String description, Roommate assignedTo, LocalDate dueDate, Household household) {
        this.title = title;
        this.description = description;
        this.assignedTo = assignedTo;
        this.dueDate = dueDate;
        this.household = household;
        this.status = ChoreStatus.OPEN;
        this.createdAt = LocalDateTime.now();
        this.rotating = false;
    }

    public void complete() {
        this.status = ChoreStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Roommate getAssignedTo() {
        return assignedTo;
    }

    public Household getHousehold() {
        return household;
    }

    public ChoreStatus getStatus() {
        return status;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public boolean isRotating() {
        return rotating;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAssignedTo(Roommate assignedTo) {
        this.assignedTo = assignedTo;
    }

    public void setHousehold(Household household) {
        this.household = household;
    }

    public void setStatus(ChoreStatus status) {
        this.status = status;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public void setRotating(boolean rotating) {
        this.rotating = rotating;
    }

    public String getFormattedDueDate() {
        return dueDate == null ? "No due date" : dueDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));
    }
}
