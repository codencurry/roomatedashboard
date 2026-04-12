package com.tammam.roomatedash.controller;

import com.tammam.roomatedash.dto.GroceryItemForm;
import com.tammam.roomatedash.exception.DashboardException;
import com.tammam.roomatedash.service.GroceryService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class GroceryController {

    private final GroceryService groceryService;
    private final DashboardModelBuilder dashboardModelBuilder;

    public GroceryController(GroceryService groceryService, DashboardModelBuilder dashboardModelBuilder) {
        this.groceryService = groceryService;
        this.dashboardModelBuilder = dashboardModelBuilder;
    }

    @PostMapping("/groceries")
    public String addItem(@Valid @ModelAttribute("groceryItemForm") GroceryItemForm form,
                          BindingResult bindingResult,
                          Model model,
                          HttpSession session) {
        if (bindingResult.hasErrors()) {
            dashboardModelBuilder.addDashboardPageModel(model, session);
            return "dashboard";
        }

        try {
            groceryService.addItem(form);
        } catch (DashboardException e) {
            dashboardModelBuilder.addDashboardPageModel(model, session);
            model.addAttribute("dashboardError", e.getMessage());
            return "dashboard";
        }

        return "redirect:/";
    }

    @PostMapping("/groceries/{id}/purchase")
    public String markPurchased(@PathVariable Long id) {
        groceryService.markPurchased(id);
        return "redirect:/";
    }

    @PostMapping("/groceries/{id}/update")
    public String updateItem(@PathVariable Long id,
                             @Valid @ModelAttribute("groceryItemForm") GroceryItemForm form,
                             BindingResult bindingResult,
                             Model model,
                             HttpSession session) {
        if (bindingResult.hasErrors()) {
            dashboardModelBuilder.addDashboardPageModel(model, session);
            return "dashboard";
        }

        groceryService.updateItem(id, form);
        return "redirect:/";
    }

    @PostMapping("/groceries/{id}/delete")
    public String deleteItem(@PathVariable Long id) {
        groceryService.deleteItem(id);
        return "redirect:/";
    }

    @PostMapping("/groceries/reset")
    public String resetPurchasedItems() {
        groceryService.resetPurchasedItems();
        return "redirect:/";
    }
}
