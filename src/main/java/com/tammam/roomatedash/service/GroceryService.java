package com.tammam.roomatedash.service;

import com.tammam.roomatedash.dto.GroceryItemForm;
import com.tammam.roomatedash.exception.ResourceNotFoundException;
import com.tammam.roomatedash.model.GroceryItem;
import com.tammam.roomatedash.model.GroceryItemStatus;
import com.tammam.roomatedash.model.Household;
import com.tammam.roomatedash.model.Roommate;
import com.tammam.roomatedash.repo.GroceryItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
public class GroceryService {

    private final RoommateService roommateService;
    private final GroceryItemRepository groceryRepo;
    private final HouseholdContext householdContext;
    private final HouseholdService householdService;
    private final ActivityLogService activityLogService;

    public GroceryService(RoommateService roommateService,
                          GroceryItemRepository groceryRepo,
                          HouseholdContext householdContext,
                          HouseholdService householdService,
                          ActivityLogService activityLogService) {
        this.roommateService = roommateService;
        this.groceryRepo = groceryRepo;
        this.householdContext = householdContext;
        this.householdService = householdService;
        this.activityLogService = activityLogService;
    }

    @Transactional
    public void addItem(GroceryItemForm form) {
        Roommate assignedTo = form.getAssignedToId() == null
                ? null
                : roommateService.getRoommate(form.getAssignedToId(), "Assigned roommate not found.");
        String quantity = form.getQuantity() == null || form.getQuantity().trim().isEmpty()
                ? null
                : form.getQuantity().trim();
        Long householdId = householdContext.getCurrentHouseholdId().orElse(null);
        Household household = householdId == null ? null : householdService.findHousehold(householdId);
        groceryRepo.save(new GroceryItem(form.getName().trim(), quantity, assignedTo, household));
        activityLogService.log("GROCERY_ADDED", "Added grocery item " + form.getName().trim() + ".");
    }

    @Transactional
    public void markPurchased(Long id) {
        GroceryItem item = groceryRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Grocery item not found."));
        item.markPurchased();
        activityLogService.log("GROCERY_PURCHASED", "Marked " + item.getName() + " purchased.");
    }

    @Transactional
    public void updateItem(Long id, GroceryItemForm form) {
        GroceryItem item = groceryRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Grocery item not found."));
        Roommate assignedTo = form.getAssignedToId() == null
                ? null
                : roommateService.getRoommate(form.getAssignedToId(), "Assigned roommate not found.");
        item.setName(form.getName().trim());
        item.setQuantity(form.getQuantity() == null || form.getQuantity().trim().isEmpty() ? null : form.getQuantity().trim());
        item.setAssignedTo(assignedTo);
        activityLogService.log("GROCERY_UPDATED", "Updated grocery item " + item.getName() + ".");
    }

    @Transactional
    public void deleteItem(Long id) {
        GroceryItem item = groceryRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Grocery item not found."));
        String name = item.getName();
        groceryRepo.delete(item);
        activityLogService.log("GROCERY_REMOVED", "Removed grocery item " + name + ".");
    }

    @Transactional
    public void resetPurchasedItems() {
        for (GroceryItem item : getItems()) {
            if (item.getStatus() == GroceryItemStatus.PURCHASED) {
                groceryRepo.delete(item);
            }
        }
        activityLogService.log("GROCERY_RESET", "Reset purchased grocery items after a shopping trip.");
    }

    @Transactional(readOnly = true)
    public List<GroceryItem> getItems() {
        List<GroceryItem> items = householdContext.getCurrentHouseholdId()
                .map(groceryRepo::findByHouseholdId)
                .orElseGet(groceryRepo::findAll);
        items.sort(Comparator
                .comparing((GroceryItem item) -> item.getStatus() == GroceryItemStatus.PURCHASED)
                .thenComparing(GroceryItem::getCreatedAt, Comparator.reverseOrder()));
        return items;
    }
}
