package com.tammam.roomatedash.controller;

import com.tammam.roomatedash.dto.HouseholdForm;
import com.tammam.roomatedash.dto.JoinHouseholdForm;
import com.tammam.roomatedash.exception.DashboardException;
import com.tammam.roomatedash.service.HouseholdService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class HouseholdController {

    private final HouseholdService householdService;

    public HouseholdController(HouseholdService householdService) {
        this.householdService = householdService;
    }

    @GetMapping("/households/new")
    public String newHousehold(HttpSession session, Model model) {
        if (householdService.ensureCurrentHousehold(session)) {
            return "redirect:/";
        }

        model.addAttribute("householdForm", new HouseholdForm());
        return "household-new";
    }

    @PostMapping("/households")
    public String createHousehold(@Valid @ModelAttribute("householdForm") HouseholdForm form,
                                  BindingResult bindingResult,
                                  HttpSession session,
                                  Model model) {
        if (bindingResult.hasErrors()) {
            return "household-new";
        }

        try {
            householdService.createHousehold(form, session);
            return "redirect:/";
        } catch (DashboardException e) {
            model.addAttribute("householdError", e.getMessage());
            return "household-new";
        }
    }

    @GetMapping("/households/join")
    public String joinHouseholdPage(Model model) {
        model.addAttribute("joinHouseholdForm", new JoinHouseholdForm());
        return "household-join";
    }

    @PostMapping("/households/join")
    public String joinHousehold(@Valid @ModelAttribute("joinHouseholdForm") JoinHouseholdForm form,
                                BindingResult bindingResult,
                                HttpSession session,
                                Model model) {
        if (bindingResult.hasErrors()) {
            return "household-join";
        }

        try {
            householdService.joinHousehold(form, session);
            return "redirect:/";
        } catch (DashboardException e) {
            model.addAttribute("householdError", e.getMessage());
            return "household-join";
        }
    }

    @PostMapping("/households/{id}/switch")
    public String switchHousehold(@PathVariable Long id, HttpSession session) {
        householdService.switchHousehold(id, session);
        return "redirect:/";
    }
}
