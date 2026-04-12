package com.tammam.roomatedash.service;

import com.tammam.roomatedash.model.Expense;
import com.tammam.roomatedash.model.ExpenseSplit;
import com.tammam.roomatedash.model.Payment;
import com.tammam.roomatedash.model.Roommate;
import com.tammam.roomatedash.repo.ExpenseRepository;
import com.tammam.roomatedash.repo.ExpenseSplitRepository;
import com.tammam.roomatedash.repo.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class BalanceService {

    private final RoommateService roommateService;
    private final ExpenseRepository expenseRepo;
    private final ExpenseSplitRepository splitRepo;
    private final PaymentRepository paymentRepo;
    private final HouseholdContext householdContext;

    public BalanceService(RoommateService roommateService,
                          ExpenseRepository expenseRepo,
                          ExpenseSplitRepository splitRepo,
                          PaymentRepository paymentRepo,
                          HouseholdContext householdContext) {
        this.roommateService = roommateService;
        this.expenseRepo = expenseRepo;
        this.splitRepo = splitRepo;
        this.paymentRepo = paymentRepo;
        this.householdContext = householdContext;
    }

    @Transactional(readOnly = true)
    public Map<Roommate, BigDecimal> getBalances() {
        Map<Long, BigDecimal> paid = new HashMap<>();
        Map<Long, BigDecimal> owes = new HashMap<>();

        List<Expense> expenses = householdContext.getCurrentHouseholdId()
                .map(expenseRepo::findByHouseholdId)
                .orElseGet(expenseRepo::findAll);
        for (Expense expense : expenses) {
            paid.merge(expense.getPayer().getId(), expense.getAmount(), BigDecimal::add);
        }

        List<ExpenseSplit> splits = householdContext.getCurrentHouseholdId()
                .map(id -> expenses.stream()
                        .flatMap(expense -> splitRepo.findByExpenseIdOrderByRoommate_NameAsc(expense.getId()).stream())
                        .toList())
                .orElseGet(splitRepo::findAll);
        for (ExpenseSplit split : splits) {
            owes.merge(split.getRoommate().getId(), split.getShare(), BigDecimal::add);
        }

        List<Payment> payments = householdContext.getCurrentHouseholdId()
                .map(paymentRepo::findByHouseholdId)
                .orElseGet(paymentRepo::findAll);
        for (Payment payment : payments) {
            paid.merge(payment.getFromRoommate().getId(), payment.getAmount(), BigDecimal::add);
            owes.merge(payment.getToRoommate().getId(), payment.getAmount(), BigDecimal::add);
        }

        Map<Roommate, BigDecimal> balances = new LinkedHashMap<>();
        for (Roommate roommate : roommateService.getRoommates()) {
            BigDecimal totalPaid = paid.getOrDefault(roommate.getId(), BigDecimal.ZERO);
            BigDecimal totalOwed = owes.getOrDefault(roommate.getId(), BigDecimal.ZERO);
            balances.put(roommate, totalPaid.subtract(totalOwed).setScale(2, RoundingMode.HALF_UP));
        }

        return balances;
    }
}
