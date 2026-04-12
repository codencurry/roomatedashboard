package com.tammam.roomatedash.service;

import com.tammam.roomatedash.dto.ExpenseForm;
import com.tammam.roomatedash.exception.DashboardException;
import com.tammam.roomatedash.model.Expense;
import com.tammam.roomatedash.model.ExpenseSplit;
import com.tammam.roomatedash.model.Household;
import com.tammam.roomatedash.model.Roommate;
import com.tammam.roomatedash.repo.ExpenseRepository;
import com.tammam.roomatedash.repo.ExpenseSplitRepository;
import com.tammam.roomatedash.repo.RoommateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;

@Service
public class ExpenseService {

    private final RoommateService roommateService;
    private final RoommateRepository roommateRepo;
    private final ExpenseRepository expenseRepo;
    private final ExpenseSplitRepository splitRepo;
    private final HouseholdContext householdContext;
    private final HouseholdService householdService;
    private final ActivityLogService activityLogService;

    public ExpenseService(RoommateService roommateService,
                          RoommateRepository roommateRepo,
                          ExpenseRepository expenseRepo,
                          ExpenseSplitRepository splitRepo,
                          HouseholdContext householdContext,
                          HouseholdService householdService,
                          ActivityLogService activityLogService) {
        this.roommateService = roommateService;
        this.roommateRepo = roommateRepo;
        this.expenseRepo = expenseRepo;
        this.splitRepo = splitRepo;
        this.householdContext = householdContext;
        this.householdService = householdService;
        this.activityLogService = activityLogService;
    }

    @Transactional
    public void addExpense(ExpenseForm form) {
        Roommate payer = roommateService.getRoommate(form.getPayerId(), "Payer not found.");

        List<Roommate> participants = roommateRepo.findAllById(form.getSplitRoommateIds());
        if (participants.isEmpty()) {
            throw new DashboardException("Select at least one roommate to split with.");
        }

        if (participants.size() != form.getSplitRoommateIds().size()) {
            throw new DashboardException("One or more selected roommates could not be found.");
        }

        Long householdId = householdContext.getCurrentHouseholdId().orElse(null);
        Household household = householdId == null ? null : householdService.findHousehold(householdId);
        String splitType = "CUSTOM".equalsIgnoreCase(form.getSplitType()) ? "CUSTOM" : "EQUAL";

        Expense expense = expenseRepo.save(new Expense(
                form.getDescription().trim(),
                form.getAmount(),
                payer,
                household,
                form.getDueDate(),
                splitType
        ));
        expense.setRecurring(form.isRecurring());
        expense.setRecurrenceInterval(form.isRecurring() ? "MONTHLY" : null);

        if ("CUSTOM".equals(splitType)) {
            saveCustomSplits(form, expense, participants);
            activityLogService.log("EXPENSE_ADDED", "Added custom-split bill " + expense.getDescription() + ".");
            return;
        }

        BigDecimal count = new BigDecimal(participants.size());
        BigDecimal share = form.getAmount().divide(count, 2, RoundingMode.HALF_UP);
        BigDecimal percentage = new BigDecimal("100.00").divide(count, 2, RoundingMode.HALF_UP);
        for (Roommate roommate : participants) {
            splitRepo.save(new ExpenseSplit(expense, roommate, share, percentage));
        }
        activityLogService.log("EXPENSE_ADDED", "Added bill " + expense.getDescription() + ".");
    }

    @Transactional(readOnly = true)
    public List<Expense> getExpenses() {
        List<Expense> list = householdContext.getCurrentHouseholdId()
                .map(expenseRepo::findByHouseholdId)
                .orElseGet(expenseRepo::findAll);
        list.sort(Comparator.comparing(Expense::getCreatedAt).reversed());
        return list;
    }

    @Transactional(readOnly = true)
    public List<ExpenseSplit> getSplits(Long expenseId) {
        return splitRepo.findByExpenseIdOrderByRoommate_NameAsc(expenseId);
    }

    @Transactional
    public void markSplitPaid(Long splitId) {
        ExpenseSplit split = splitRepo.findById(splitId)
                .orElseThrow(() -> new DashboardException("Expense split not found."));
        split.markPaid();
        activityLogService.log("BILL_SPLIT_PAID", split.getRoommate().getName() + " marked their portion of " + split.getExpense().getDescription() + " paid.");
    }

    @Transactional
    public void createNextRecurringBill(Long expenseId) {
        Expense original = expenseRepo.findById(expenseId)
                .orElseThrow(() -> new DashboardException("Expense not found."));
        if (!original.isRecurring()) {
            throw new DashboardException("Only recurring bills can generate the next bill.");
        }

        Expense next = new Expense(
                original.getDescription(),
                original.getAmount(),
                original.getPayer(),
                original.getHousehold(),
                original.getDueDate() == null ? null : original.getDueDate().plusMonths(1),
                original.getSplitType()
        );
        next.setRecurring(true);
        next.setRecurrenceInterval("MONTHLY");
        expenseRepo.save(next);

        for (ExpenseSplit split : splitRepo.findByExpenseIdOrderByRoommate_NameAsc(original.getId())) {
            splitRepo.save(new ExpenseSplit(next, split.getRoommate(), split.getShare(), split.getPercentage()));
        }
        activityLogService.log("RECURRING_BILL_CREATED", "Generated next recurring bill for " + original.getDescription() + ".");
    }

    private void saveCustomSplits(ExpenseForm form, Expense expense, List<Roommate> participants) {
        BigDecimal totalPercentage = BigDecimal.ZERO;
        for (Roommate roommate : participants) {
            BigDecimal percentage = form.getSplitPercentages().get(roommate.getId());
            if (percentage == null || percentage.compareTo(BigDecimal.ZERO) <= 0) {
                throw new DashboardException("Enter a positive percentage for each selected roommate.");
            }
            totalPercentage = totalPercentage.add(percentage);
        }

        if (totalPercentage.setScale(2, RoundingMode.HALF_UP).compareTo(new BigDecimal("100.00")) != 0) {
            throw new DashboardException("Custom split percentages must total 100.");
        }

        for (Roommate roommate : participants) {
            BigDecimal percentage = form.getSplitPercentages().get(roommate.getId());
            BigDecimal share = form.getAmount()
                    .multiply(percentage)
                    .divide(new BigDecimal("100.00"), 2, RoundingMode.HALF_UP);
            splitRepo.save(new ExpenseSplit(expense, roommate, share, percentage));
        }
    }
}
