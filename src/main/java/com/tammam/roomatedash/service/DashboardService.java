package com.tammam.roomatedash.service;

import com.tammam.roomatedash.dto.ExpenseForm;
import com.tammam.roomatedash.dto.PaymentForm;
import com.tammam.roomatedash.model.Expense;
import com.tammam.roomatedash.model.ExpenseSplit;
import com.tammam.roomatedash.model.Roommate;
import com.tammam.roomatedash.repo.ExpenseRepository;
import com.tammam.roomatedash.repo.ExpenseSplitRepository;
import com.tammam.roomatedash.repo.RoommateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class DashboardService {

    private final RoommateRepository roommateRepo;
    private final ExpenseRepository expenseRepo;
    private final ExpenseSplitRepository splitRepo;

    public DashboardService(RoommateRepository roommateRepo,
                            ExpenseRepository expenseRepo,
                            ExpenseSplitRepository splitRepo) {
        this.roommateRepo = roommateRepo;
        this.expenseRepo = expenseRepo;
        this.splitRepo = splitRepo;
    }

    @Transactional
    public void addRoommate(String name) {
        String trimmed = name == null ? "" : name.trim();
        if (trimmed.isEmpty()) return;

        roommateRepo.findByNameIgnoreCase(trimmed).ifPresent(r -> {
            throw new IllegalArgumentException("Roommate already exists.");
        });

        roommateRepo.save(new Roommate(trimmed));
    }

    @Transactional
    public void addExpense(ExpenseForm form) {
        Roommate payer = roommateRepo.findById(form.getPayerId())
                .orElseThrow(() -> new IllegalArgumentException("Payer not found"));

        List<Roommate> participants = roommateRepo.findAllById(form.getSplitRoommateIds());
        if (participants.isEmpty()) {
            throw new IllegalArgumentException("Select at least one roommate to split with.");
        }

        Expense expense = expenseRepo.save(new Expense(form.getDescription().trim(), form.getAmount(), payer));

        BigDecimal count = new BigDecimal(participants.size());
        BigDecimal share = form.getAmount().divide(count, 2, RoundingMode.HALF_UP);

        for (Roommate r : participants) {
            splitRepo.save(new ExpenseSplit(expense, r, share));
        }
    }

    @Transactional
    public void recordPayment(PaymentForm form) {
        if (form.getFromRoommateId().equals(form.getToRoommateId())) {
            throw new IllegalArgumentException("From and To cannot be the same person.");
        }

        Roommate from = roommateRepo.findById(form.getFromRoommateId())
                .orElseThrow(() -> new IllegalArgumentException("From roommate not found"));

        Roommate to = roommateRepo.findById(form.getToRoommateId())
                .orElseThrow(() -> new IllegalArgumentException("To roommate not found"));

        String desc = "Payment: " + from.getName() + " -> " + to.getName();
        Expense expense = expenseRepo.save(new Expense(desc, form.getAmount(), from));
        splitRepo.save(new ExpenseSplit(expense, to, form.getAmount()));
    }

    public List<Roommate> getRoommates() {
        List<Roommate> list = roommateRepo.findAll();
        list.sort(Comparator.comparing(Roommate::getName));
        return list;
    }

    public List<Expense> getExpenses() {
        List<Expense> list = expenseRepo.findAll();
        list.sort(Comparator.comparing(Expense::getCreatedAt).reversed());
        return list;
    }

    public Map<Roommate, BigDecimal> getBalances() {
        Map<Long, BigDecimal> paid = new HashMap<>();
        Map<Long, BigDecimal> owes = new HashMap<>();

        for (Expense e : expenseRepo.findAll()) {
            paid.merge(e.getPayer().getId(), e.getAmount(), BigDecimal::add);
        }

        for (ExpenseSplit s : splitRepo.findAll()) {
            owes.merge(s.getRoommate().getId(), s.getShare(), BigDecimal::add);
        }

        Map<Roommate, BigDecimal> out = new LinkedHashMap<>();
        for (Roommate r : getRoommates()) {
            BigDecimal p = paid.getOrDefault(r.getId(), BigDecimal.ZERO);
            BigDecimal o = owes.getOrDefault(r.getId(), BigDecimal.ZERO);
            out.put(r, p.subtract(o).setScale(2, RoundingMode.HALF_UP));
        }

        return out;
    }
}
