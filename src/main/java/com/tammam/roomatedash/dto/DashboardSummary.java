package com.tammam.roomatedash.dto;

import java.math.BigDecimal;

public class DashboardSummary {

    private final long billsDueSoon;
    private final long unpaidBillPortions;
    private final long overdueChores;
    private final long pendingGroceries;
    private final String roommateOwingMost;
    private final BigDecimal largestAmountOwed;

    public DashboardSummary(long billsDueSoon,
                            long unpaidBillPortions,
                            long overdueChores,
                            long pendingGroceries,
                            String roommateOwingMost,
                            BigDecimal largestAmountOwed) {
        this.billsDueSoon = billsDueSoon;
        this.unpaidBillPortions = unpaidBillPortions;
        this.overdueChores = overdueChores;
        this.pendingGroceries = pendingGroceries;
        this.roommateOwingMost = roommateOwingMost;
        this.largestAmountOwed = largestAmountOwed;
    }

    public long getBillsDueSoon() {
        return billsDueSoon;
    }

    public long getUnpaidBillPortions() {
        return unpaidBillPortions;
    }

    public long getOverdueChores() {
        return overdueChores;
    }

    public long getPendingGroceries() {
        return pendingGroceries;
    }

    public String getRoommateOwingMost() {
        return roommateOwingMost;
    }

    public BigDecimal getLargestAmountOwed() {
        return largestAmountOwed;
    }
}
