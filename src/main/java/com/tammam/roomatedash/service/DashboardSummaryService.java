package com.tammam.roomatedash.service;

import com.tammam.roomatedash.dto.DashboardSummary;
import com.tammam.roomatedash.model.ChoreStatus;
import com.tammam.roomatedash.model.GroceryItemStatus;
import com.tammam.roomatedash.model.Roommate;
import com.tammam.roomatedash.repo.ChoreRepository;
import com.tammam.roomatedash.repo.ExpenseRepository;
import com.tammam.roomatedash.repo.ExpenseSplitRepository;
import com.tammam.roomatedash.repo.GroceryItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Service
public class DashboardSummaryService {

    private final HouseholdContext householdContext;
    private final ExpenseRepository expenseRepo;
    private final ExpenseSplitRepository splitRepo;
    private final ChoreRepository choreRepo;
    private final GroceryItemRepository groceryRepo;
    private final BalanceService balanceService;

    public DashboardSummaryService(HouseholdContext householdContext,
                                   ExpenseRepository expenseRepo,
                                   ExpenseSplitRepository splitRepo,
                                   ChoreRepository choreRepo,
                                   GroceryItemRepository groceryRepo,
                                   BalanceService balanceService) {
        this.householdContext = householdContext;
        this.expenseRepo = expenseRepo;
        this.splitRepo = splitRepo;
        this.choreRepo = choreRepo;
        this.groceryRepo = groceryRepo;
        this.balanceService = balanceService;
    }

    @Transactional(readOnly = true)
    public DashboardSummary getSummary() {
        Long householdId = householdContext.getCurrentHouseholdId().orElse(null);
        long billsDueSoon = householdId == null ? 0 : expenseRepo.countByHouseholdIdAndDueDateLessThanEqual(householdId, LocalDate.now().plusDays(7));
        long unpaidBillPortions = householdId == null ? 0 : splitRepo.countByExpenseHouseholdIdAndPaidFalse(householdId);
        long overdueChores = householdId == null ? 0 : choreRepo.countByHouseholdIdAndStatusAndDueDateLessThan(householdId, ChoreStatus.OPEN, LocalDate.now());
        long pendingGroceries = householdId == null ? 0 : groceryRepo.countByHouseholdIdAndStatus(householdId, GroceryItemStatus.NEEDED);

        String roommateOwingMost = "None";
        BigDecimal largestAmountOwed = BigDecimal.ZERO;
        for (Map.Entry<Roommate, BigDecimal> entry : balanceService.getBalances().entrySet()) {
            if (entry.getValue().compareTo(largestAmountOwed.negate()) < 0) {
                roommateOwingMost = entry.getKey().getName();
                largestAmountOwed = entry.getValue().abs();
            }
        }

        return new DashboardSummary(billsDueSoon, unpaidBillPortions, overdueChores, pendingGroceries, roommateOwingMost, largestAmountOwed);
    }
}
