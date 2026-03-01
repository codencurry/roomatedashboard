package com.tammam.roomatedash.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ExpenseForm {

    @NotBlank(message = "Description required")
    private String description;

    @NotNull(message = "Amount required")
    @DecimalMin(value = "0.01", message = "Amount must be at least 0.01")
    private BigDecimal amount;

    @NotNull(message = "Payer required")
    private Long payerId;

    @Size(min = 1, message = "Select at least one roommate")
    private List<Long> splitRoommateIds = new ArrayList<>();

    public String getDescription() { return description; }
    public BigDecimal getAmount() { return amount; }
    public Long getPayerId() { return payerId; }
    public List<Long> getSplitRoommateIds() { return splitRoommateIds; }

    public void setDescription(String description) { this.description = description; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setPayerId(Long payerId) { this.payerId = payerId; }
    public void setSplitRoommateIds(List<Long> splitRoommateIds) { this.splitRoommateIds = splitRoommateIds; }
}
