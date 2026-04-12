package com.tammam.roomatedash.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Household household;

    @ManyToOne
    private AppUser actor;

    @Column(nullable = false, length = 40)
    private String actionType;

    @Column(nullable = false, length = 300)
    private String message;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public ActivityLog() {
    }

    public ActivityLog(Household household, AppUser actor, String actionType, String message) {
        this.household = household;
        this.actor = actor;
        this.actionType = actionType;
        this.message = message;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Household getHousehold() {
        return household;
    }

    public AppUser getActor() {
        return actor;
    }

    public String getActionType() {
        return actionType;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setHousehold(Household household) {
        this.household = household;
    }

    public void setActor(AppUser actor) {
        this.actor = actor;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getFormattedCreatedAt() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM dd, yyyy - hh:mm a");
        return createdAt.format(fmt);
    }
}
