package com.tammam.roomatedash.dto;

import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dueDate;

    private String splitType = "EQUAL";

    private boolean recurring;

    private Map<Long, BigDecimal> splitPercentages = new HashMap<>();

    public String getDescription() { return description; }
    public BigDecimal getAmount() { return amount; }
    public Long getPayerId() { return payerId; }
    public List<Long> getSplitRoommateIds() { return splitRoommateIds; }
    public LocalDate getDueDate() { return dueDate; }
    public String getSplitType() { return splitType; }
    public boolean isRecurring() { return recurring; }
    public Map<Long, BigDecimal> getSplitPercentages() { return splitPercentages; }

    public void setDescription(String description) { this.description = description; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setPayerId(Long payerId) { this.payerId = payerId; }
    public void setSplitRoommateIds(List<Long> splitRoommateIds) {
        this.splitRoommateIds = splitRoommateIds == null ? new ArrayList<>() : splitRoommateIds;
    }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public void setSplitType(String splitType) { this.splitType = splitType; }
    public void setRecurring(boolean recurring) { this.recurring = recurring; }
    public void setSplitPercentages(Map<Long, BigDecimal> splitPercentages) {
        this.splitPercentages = splitPercentages == null ? new HashMap<>() : splitPercentages;
    }
}
