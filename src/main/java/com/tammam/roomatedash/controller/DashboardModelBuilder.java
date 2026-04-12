package com.tammam.roomatedash.controller;

import com.tammam.roomatedash.dto.ChoreForm;
import com.tammam.roomatedash.dto.ExpenseForm;
import com.tammam.roomatedash.dto.GroceryItemForm;
import com.tammam.roomatedash.dto.PaymentForm;
import com.tammam.roomatedash.dto.RoommateForm;
import com.tammam.roomatedash.service.DashboardService;
import com.tammam.roomatedash.service.DashboardSummaryService;
import com.tammam.roomatedash.service.ActivityLogService;
import com.tammam.roomatedash.service.ExpenseService;
import com.tammam.roomatedash.service.HouseholdService;
import com.tammam.roomatedash.service.MockAuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

@Component
public class DashboardModelBuilder {

    private final DashboardService dashboardService;
    private final MockAuthService authService;
    private final HouseholdService householdService;
    private final ActivityLogService activityLogService;
    private final DashboardSummaryService summaryService;
    private final ExpenseService expenseService;

    public DashboardModelBuilder(DashboardService dashboardService,
                                 MockAuthService authService,
                                 HouseholdService householdService,
                                 ActivityLogService activityLogService,
                                 DashboardSummaryService summaryService,
                                 ExpenseService expenseService) {
        this.dashboardService = dashboardService;
        this.authService = authService;
        this.householdService = householdService;
        this.activityLogService = activityLogService;
        this.summaryService = summaryService;
        this.expenseService = expenseService;
    }

    public void addDashboardPageModel(Model model, HttpSession session) {
        addDashboardData(model, session);
        addMissingForms(model);
    }

    public void addDashboardData(Model model, HttpSession session) {
        authService.getCurrentUser(session)
                .ifPresent(user -> model.addAttribute("currentUser", user));
        model.addAttribute("householdMemberships", householdService.getMemberships(session));
        authService.getCurrentHouseholdId(session)
                .ifPresent(id -> model.addAttribute("currentHousehold", householdService.findHousehold(id)));
        model.addAttribute("roommates", dashboardService.getRoommates());
        model.addAttribute("expenses", dashboardService.getExpenses());
        model.addAttribute("expenseSplits", dashboardService.getExpenses().stream()
                .collect(java.util.stream.Collectors.toMap(
                        expense -> expense.getId(),
                        expense -> expenseService.getSplits(expense.getId())
                )));
        model.addAttribute("payments", dashboardService.getPayments());
        model.addAttribute("chores", dashboardService.getChores());
        model.addAttribute("groceryItems", dashboardService.getGroceryItems());
        model.addAttribute("balances", dashboardService.getBalances());
        model.addAttribute("recentActivity", activityLogService.getRecentActivity());
        model.addAttribute("summary", summaryService.getSummary());
    }

    private void addMissingForms(Model model) {
        if (!model.containsAttribute("roommateForm")) {
            model.addAttribute("roommateForm", new RoommateForm());
        }
        if (!model.containsAttribute("expenseForm")) {
            model.addAttribute("expenseForm", new ExpenseForm());
        }
        if (!model.containsAttribute("paymentForm")) {
            model.addAttribute("paymentForm", new PaymentForm());
        }
        if (!model.containsAttribute("choreForm")) {
            model.addAttribute("choreForm", new ChoreForm());
        }
        if (!model.containsAttribute("groceryItemForm")) {
            model.addAttribute("groceryItemForm", new GroceryItemForm());
        }
    }
}
