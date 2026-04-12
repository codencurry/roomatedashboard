package com.tammam.roomatedash.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class ChoreForm {

    @NotBlank(message = "Chore title is required")
    @Size(max = 120, message = "Chore title must be 120 characters or fewer")
    private String title;

    @Size(max = 300, message = "Description must be 300 characters or fewer")
    private String description;

    @NotNull(message = "Assigned roommate is required")
    private Long assignedToId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dueDate;

    private boolean rotating;

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Long getAssignedToId() {
        return assignedToId;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public boolean isRotating() {
        return rotating;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAssignedToId(Long assignedToId) {
        this.assignedToId = assignedToId;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public void setRotating(boolean rotating) {
        this.rotating = rotating;
    }
}
