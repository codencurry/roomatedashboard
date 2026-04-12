package com.tammam.roomatedash.controller;

import com.tammam.roomatedash.service.ExpenseService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ExpenseSplitController {

    private final ExpenseService expenseService;

    public ExpenseSplitController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping("/expense-splits/{id}/paid")
    public String markPaid(@PathVariable Long id) {
        expenseService.markSplitPaid(id);
        return "redirect:/";
    }

    @PostMapping("/expenses/{id}/generate-next")
    public String generateNextRecurringBill(@PathVariable Long id) {
        expenseService.createNextRecurringBill(id);
        return "redirect:/";
    }
}
