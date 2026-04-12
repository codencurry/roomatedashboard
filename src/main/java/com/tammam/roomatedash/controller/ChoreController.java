package com.tammam.roomatedash.controller;

import com.tammam.roomatedash.dto.ChoreForm;
import com.tammam.roomatedash.exception.DashboardException;
import com.tammam.roomatedash.service.ChoreService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ChoreController {

    private final ChoreService choreService;
    private final DashboardModelBuilder dashboardModelBuilder;

    public ChoreController(ChoreService choreService, DashboardModelBuilder dashboardModelBuilder) {
        this.choreService = choreService;
        this.dashboardModelBuilder = dashboardModelBuilder;
    }

    @PostMapping("/chores")
    public String addChore(@Valid @ModelAttribute("choreForm") ChoreForm form,
                           BindingResult bindingResult,
                           Model model,
                           HttpSession session) {
        if (bindingResult.hasErrors()) {
            dashboardModelBuilder.addDashboardPageModel(model, session);
            return "dashboard";
        }

        try {
            choreService.addChore(form);
        } catch (DashboardException e) {
            dashboardModelBuilder.addDashboardPageModel(model, session);
            model.addAttribute("dashboardError", e.getMessage());
            return "dashboard";
        }

        return "redirect:/";
    }

    @PostMapping("/chores/{id}/complete")
    public String completeChore(@PathVariable Long id) {
        choreService.completeChore(id);
        return "redirect:/";
    }

    @PostMapping("/chores/rotate-weekly")
    public String rotateWeeklyChores() {
        choreService.rotateWeeklyChores();
        return "redirect:/";
    }
}
