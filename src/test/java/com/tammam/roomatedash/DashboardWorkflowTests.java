package com.tammam.roomatedash;

import com.tammam.roomatedash.dto.ChoreForm;
import com.tammam.roomatedash.dto.ExpenseForm;
import com.tammam.roomatedash.dto.GroceryItemForm;
import com.tammam.roomatedash.dto.PaymentForm;
import com.tammam.roomatedash.model.Chore;
import com.tammam.roomatedash.model.ChoreStatus;
import com.tammam.roomatedash.model.GroceryItem;
import com.tammam.roomatedash.model.GroceryItemStatus;
import com.tammam.roomatedash.model.Roommate;
import com.tammam.roomatedash.repo.ChoreRepository;
import com.tammam.roomatedash.repo.ExpenseRepository;
import com.tammam.roomatedash.repo.ExpenseSplitRepository;
import com.tammam.roomatedash.repo.GroceryItemRepository;
import com.tammam.roomatedash.repo.PaymentRepository;
import com.tammam.roomatedash.repo.RoommateRepository;
import com.tammam.roomatedash.service.BalanceService;
import com.tammam.roomatedash.service.ChoreService;
import com.tammam.roomatedash.service.ExpenseService;
import com.tammam.roomatedash.service.GroceryService;
import com.tammam.roomatedash.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class DashboardWorkflowTests {

    private final RoommateRepository roommateRepo;
    private final ExpenseRepository expenseRepo;
    private final ExpenseSplitRepository splitRepo;
    private final PaymentRepository paymentRepo;
    private final ChoreRepository choreRepo;
    private final GroceryItemRepository groceryRepo;
    private final ExpenseService expenseService;
    private final PaymentService paymentService;
    private final BalanceService balanceService;
    private final ChoreService choreService;
    private final GroceryService groceryService;

    @Autowired
    DashboardWorkflowTests(RoommateRepository roommateRepo,
                           ExpenseRepository expenseRepo,
                           ExpenseSplitRepository splitRepo,
                           PaymentRepository paymentRepo,
                           ChoreRepository choreRepo,
                           GroceryItemRepository groceryRepo,
                           ExpenseService expenseService,
                           PaymentService paymentService,
                           BalanceService balanceService,
                           ChoreService choreService,
                           GroceryService groceryService) {
        this.roommateRepo = roommateRepo;
        this.expenseRepo = expenseRepo;
        this.splitRepo = splitRepo;
        this.paymentRepo = paymentRepo;
        this.choreRepo = choreRepo;
        this.groceryRepo = groceryRepo;
        this.expenseService = expenseService;
        this.paymentService = paymentService;
        this.balanceService = balanceService;
        this.choreService = choreService;
        this.groceryService = groceryService;
    }

    @BeforeEach
    void cleanDatabase() {
        splitRepo.deleteAll();
        paymentRepo.deleteAll();
        choreRepo.deleteAll();
        groceryRepo.deleteAll();
        expenseRepo.deleteAll();
        roommateRepo.deleteAll();
    }

    @Test
    void expenseSplitAndPaymentSettleBalances() {
        Roommate alice = roommateRepo.save(new Roommate("Alice"));
        Roommate bob = roommateRepo.save(new Roommate("Bob"));

        ExpenseForm expenseForm = new ExpenseForm();
        expenseForm.setDescription("Internet");
        expenseForm.setAmount(new BigDecimal("100.00"));
        expenseForm.setPayerId(alice.getId());
        expenseForm.setSplitRoommateIds(List.of(alice.getId(), bob.getId()));
        expenseService.addExpense(expenseForm);

        Map<Roommate, BigDecimal> balancesAfterExpense = balanceService.getBalances();
        assertEquals(new BigDecimal("50.00"), balanceFor(balancesAfterExpense, "Alice"));
        assertEquals(new BigDecimal("-50.00"), balanceFor(balancesAfterExpense, "Bob"));

        PaymentForm paymentForm = new PaymentForm();
        paymentForm.setFromRoommateId(bob.getId());
        paymentForm.setToRoommateId(alice.getId());
        paymentForm.setAmount(new BigDecimal("50.00"));
        paymentService.recordPayment(paymentForm);

        Map<Roommate, BigDecimal> balancesAfterPayment = balanceService.getBalances();
        assertEquals(new BigDecimal("0.00"), balanceFor(balancesAfterPayment, "Alice"));
        assertEquals(new BigDecimal("0.00"), balanceFor(balancesAfterPayment, "Bob"));
    }

    @Test
    void choreCanBeAssignedAndCompleted() {
        Roommate alice = roommateRepo.save(new Roommate("Alice"));

        ChoreForm form = new ChoreForm();
        form.setTitle("Clean kitchen");
        form.setAssignedToId(alice.getId());
        choreService.addChore(form);

        Chore chore = choreService.getChores().get(0);
        assertEquals(ChoreStatus.OPEN, chore.getStatus());
        assertEquals("Alice", chore.getAssignedTo().getName());

        choreService.completeChore(chore.getId());
        Chore completed = choreRepo.findById(chore.getId()).orElseThrow();
        assertEquals(ChoreStatus.COMPLETED, completed.getStatus());
    }

    @Test
    void groceryItemCanBeAssignedAndPurchased() {
        Roommate bob = roommateRepo.save(new Roommate("Bob"));

        GroceryItemForm form = new GroceryItemForm();
        form.setName("Eggs");
        form.setQuantity("1 dozen");
        form.setAssignedToId(bob.getId());
        groceryService.addItem(form);

        GroceryItem item = groceryService.getItems().get(0);
        assertEquals(GroceryItemStatus.NEEDED, item.getStatus());
        assertEquals("Bob", item.getAssignedTo().getName());

        groceryService.markPurchased(item.getId());
        GroceryItem purchased = groceryRepo.findById(item.getId()).orElseThrow();
        assertEquals(GroceryItemStatus.PURCHASED, purchased.getStatus());
    }

    private BigDecimal balanceFor(Map<Roommate, BigDecimal> balances, String name) {
        return balances.entrySet().stream()
                .filter(entry -> entry.getKey().getName().equals(name))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElseThrow();
    }
}
