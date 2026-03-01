package com.tammam.roomatedash.dto;

import jakarta.validation.constraints.DecimalMin;
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

    public Long getFromRoommateId() { return fromRoommateId; }
    public Long getToRoommateId() { return toRoommateId; }
    public BigDecimal getAmount() { return amount; }

    public void setFromRoommateId(Long fromRoommateId) { this.fromRoommateId = fromRoommateId; }
    public void setToRoommateId(Long toRoommateId) { this.toRoommateId = toRoommateId; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}
