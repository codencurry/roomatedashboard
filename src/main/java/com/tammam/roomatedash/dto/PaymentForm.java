package com.tammam.roomatedash.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class PaymentForm {

    @NotNull(message = "From is required")
    private Long fromRoommateId;

    @NotNull(message = "To is required")
    private Long toRoommateId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be at least 0.01")
    private BigDecimal amount;

    @Size(max = 200, message = "Note must be 200 characters or fewer")
    private String note;

    public Long getFromRoommateId() { return fromRoommateId; }
    public Long getToRoommateId() { return toRoommateId; }
    public BigDecimal getAmount() { return amount; }
    public String getNote() { return note; }

    public void setFromRoommateId(Long fromRoommateId) { this.fromRoommateId = fromRoommateId; }
    public void setToRoommateId(Long toRoommateId) { this.toRoommateId = toRoommateId; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setNote(String note) { this.note = note; }
}
