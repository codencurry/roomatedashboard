package com.tammam.roomatedash.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LoginForm {

    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email")
    @Size(max = 160, message = "Email must be 160 characters or fewer")
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
