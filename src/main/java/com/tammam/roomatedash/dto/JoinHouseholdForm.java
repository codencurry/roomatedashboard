package com.tammam.roomatedash.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class JoinHouseholdForm {

    @NotBlank(message = "Invite code is required")
    @Size(max = 12, message = "Invite code must be 12 characters or fewer")
    private String inviteCode;

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }
}
