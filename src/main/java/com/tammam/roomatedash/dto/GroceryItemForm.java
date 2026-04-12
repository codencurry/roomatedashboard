package com.tammam.roomatedash.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class GroceryItemForm {

    @NotBlank(message = "Item name is required")
    @Size(max = 120, message = "Item name must be 120 characters or fewer")
    private String name;

    @Size(max = 80, message = "Quantity must be 80 characters or fewer")
    private String quantity;

    private Long assignedToId;

    public String getName() {
        return name;
    }

    public String getQuantity() {
        return quantity;
    }

    public Long getAssignedToId() {
        return assignedToId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public void setAssignedToId(Long assignedToId) {
        this.assignedToId = assignedToId;
    }
}
