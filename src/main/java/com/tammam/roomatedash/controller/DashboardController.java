package com.tammam.roomatedash.controller;

import com.tammam.roomatedash.dto.ExpenseForm;
import com.tammam.roomatedash.dto.PaymentForm;
import com.tammam.roomatedash.dto.RoommateForm;
import com.tammam.roomatedash.exception.DashboardException;
import com.tammam.roomatedash.service.ExpenseService;
import com.tammam.roomatedash.service.PaymentService;
import com.tammam.roomatedash.service.RoommateService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class DashboardController {

    private final DashboardModelBuilder dashboardModelBuilder;
    private final RoommateService roommateService;
    private final ExpenseService expenseService;
    private final PaymentService paymentService;

    public DashboardController(DashboardModelBuilder dashboardModelBuilder,
                               RoommateService roommateService,
                               ExpenseService expenseService,
                               PaymentService paymentService) {
        this.dashboardModelBuilder = dashboardModelBuilder;
        this.roommateService = roommateService;
        this.expenseService = expenseService;
        this.paymentService = paymentService;
    }

    @GetMapping("/")
    public String dashboard(Model model, HttpSession session) {
        dashboardModelBuilder.addDashboardPageModel(model, session);
        return "dashboard";
    }

    @PostMapping("/roommates")
    public String addRoommate(@Valid @ModelAttribute("roommateForm") RoommateForm form,
                              BindingResult bindingResult,
                              Model model,
                              HttpSession session) {
        if (bindingResult.hasErrors()) {
            dashboardModelBuilder.addDashboardPageModel(model, session);
            return "dashboard";
        }

        try {
            roommateService.addRoommate(form);
        } catch (DashboardException e) {
            dashboardModelBuilder.addDashboardPageModel(model, session);
            model.addAttribute("dashboardError", e.getMessage());
            return "dashboard";
        }

        return "redirect:/";
    }

    @PostMapping("/expenses")
    public String addExpense(@Valid @ModelAttribute("expenseForm") ExpenseForm form,
                             BindingResult bindingResult,
                             Model model,
                             HttpSession session) {

        if (bindingResult.hasErrors()) {
            dashboardModelBuilder.addDashboardPageModel(model, session);
            return "dashboard";
        }

        try {
            expenseService.addExpense(form);
            return "redirect:/";
        } catch (DashboardException e) {
            dashboardModelBuilder.addDashboardPageModel(model, session);
            model.addAttribute("dashboardError", e.getMessage());
            return "dashboard";
        }
    }

    @PostMapping("/payments")
    public String recordPayment(@Valid @ModelAttribute("paymentForm") PaymentForm form,
                                BindingResult bindingResult,
                                Model model,
                                HttpSession session) {

        if (bindingResult.hasErrors()) {
            dashboardModelBuilder.addDashboardPageModel(model, session);
            return "dashboard";
        }

        try {
            paymentService.recordPayment(form);
            return "redirect:/";
        } catch (DashboardException e) {
            dashboardModelBuilder.addDashboardPageModel(model, session);
            model.addAttribute("dashboardError", e.getMessage());
            return "dashboard";
        }
    }
}
