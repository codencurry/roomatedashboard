package com.tammam.roomatedash.service;

import com.tammam.roomatedash.model.Expense;
import com.tammam.roomatedash.model.Chore;
import com.tammam.roomatedash.model.GroceryItem;
import com.tammam.roomatedash.model.Payment;
import com.tammam.roomatedash.model.Roommate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    private final RoommateService roommateService;
    private final ExpenseService expenseService;
    private final PaymentService paymentService;
    private final ChoreService choreService;
    private final GroceryService groceryService;
    private final BalanceService balanceService;

    public DashboardService(RoommateService roommateService,
                            ExpenseService expenseService,
                            PaymentService paymentService,
                            ChoreService choreService,
                            GroceryService groceryService,
                            BalanceService balanceService) {
        this.roommateService = roommateService;
        this.expenseService = expenseService;
        this.paymentService = paymentService;
        this.choreService = choreService;
        this.groceryService = groceryService;
        this.balanceService = balanceService;
    }

    public List<Roommate> getRoommates() {
        return roommateService.getRoommates();
    }

    public List<Expense> getExpenses() {
        return expenseService.getExpenses();
    }

    public List<Payment> getPayments() {
        return paymentService.getPayments();
    }

    public List<Chore> getChores() {
        return choreService.getChores();
    }

    public List<GroceryItem> getGroceryItems() {
        return groceryService.getItems();
    }

    public Map<Roommate, BigDecimal> getBalances() {
        return balanceService.getBalances();
    }
}
