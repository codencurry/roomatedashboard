package com.tammam.roomatedash.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class HouseholdForm {

    @NotBlank(message = "Household name is required")
    @Size(max = 120, message = "Household name must be 120 characters or fewer")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
