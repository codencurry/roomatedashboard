package com.tammam.roomatedash.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RoommateForm {

    @NotBlank(message = "Roommate name is required")
    @Size(max = 80, message = "Roommate name must be 80 characters or fewer")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
