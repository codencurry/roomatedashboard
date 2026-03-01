package com.tammam.roomatedash.controller;

import com.tammam.roomatedash.dto.ExpenseForm;
import com.tammam.roomatedash.dto.PaymentForm;
import com.tammam.roomatedash.service.DashboardService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class DashboardController {

    private final DashboardService service;

    public DashboardController(DashboardService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String dashboard(Model model) {
        model.addAttribute("roommates", service.getRoommates());
        model.addAttribute("expenses", service.getExpenses());
        model.addAttribute("balances", service.getBalances());
        model.addAttribute("expenseForm", new ExpenseForm());
        model.addAttribute("paymentForm", new PaymentForm());
        return "dashboard";
    }

    @PostMapping("/roommates")
    public String addRoommate(@RequestParam String name) {
        try {
            service.addRoommate(name);
        } catch (Exception ignored) {}
        return "redirect:/";
    }

    @PostMapping("/expenses")
    public String addExpense(@Valid @ModelAttribute("expenseForm") ExpenseForm form,
                             BindingResult bindingResult,
                             Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("roommates", service.getRoommates());
            model.addAttribute("expenses", service.getExpenses());
            model.addAttribute("balances", service.getBalances());
            model.addAttribute("paymentForm", new PaymentForm());
            return "dashboard";
        }

        try {
            service.addExpense(form);
            return "redirect:/";
        } catch (Exception e) {
            model.addAttribute("roommates", service.getRoommates());
            model.addAttribute("expenses", service.getExpenses());
            model.addAttribute("balances", service.getBalances());
            model.addAttribute("paymentForm", new PaymentForm());
            model.addAttribute("formError", e.getMessage());
            return "dashboard";
        }
    }

    @PostMapping("/payments")
    public String recordPayment(@Valid @ModelAttribute("paymentForm") PaymentForm form,
                                BindingResult bindingResult,
                                Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("roommates", service.getRoommates());
            model.addAttribute("expenses", service.getExpenses());
            model.addAttribute("balances", service.getBalances());
            model.addAttribute("expenseForm", new ExpenseForm());
            return "dashboard";
        }

        try {
            service.recordPayment(form);
            return "redirect:/";
        } catch (Exception e) {
            model.addAttribute("roommates", service.getRoommates());
            model.addAttribute("expenses", service.getExpenses());
            model.addAttribute("balances", service.getBalances());
            model.addAttribute("expenseForm", new ExpenseForm());
            model.addAttribute("formError", e.getMessage());
            return "dashboard";
        }
    }
}
