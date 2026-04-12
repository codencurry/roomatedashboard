package com.tammam.roomatedash.controller;

import com.tammam.roomatedash.dto.LoginForm;
import com.tammam.roomatedash.dto.RegisterForm;
import com.tammam.roomatedash.exception.DashboardException;
import com.tammam.roomatedash.model.AppUser;
import com.tammam.roomatedash.service.HouseholdService;
import com.tammam.roomatedash.service.MockAuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final MockAuthService authService;
    private final HouseholdService householdService;

    public AuthController(MockAuthService authService, HouseholdService householdService) {
        this.authService = authService;
        this.householdService = householdService;
    }

    @GetMapping("/login")
    public String loginPage(HttpSession session, Model model) {
        if (authService.isLoggedIn(session)) {
            return "redirect:/";
        }

        model.addAttribute("loginForm", new LoginForm());
        return "login";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("loginForm") LoginForm form,
                        BindingResult bindingResult,
                        HttpSession session,
                        Model model) {
        if (bindingResult.hasErrors()) {
            return "login";
        }

        try {
            AppUser user = authService.login(form);
            authService.storeLogin(session, user);
            householdService.ensureOrCreateDefaultHousehold(session);
            return "redirect:/";
        } catch (DashboardException e) {
            model.addAttribute("authError", e.getMessage());
            return "login";
        }
    }

    @GetMapping("/register")
    public String registerPage(HttpSession session, Model model) {
        if (authService.isLoggedIn(session)) {
            return "redirect:/";
        }

        model.addAttribute("registerForm", new RegisterForm());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registerForm") RegisterForm form,
                           BindingResult bindingResult,
                           HttpSession session,
                           Model model) {
        if (bindingResult.hasErrors()) {
            return "register";
        }

        try {
            AppUser user = authService.register(form);
            authService.storeLogin(session, user);
            householdService.ensureOrCreateDefaultHousehold(session);
            return "redirect:/";
        } catch (DashboardException e) {
            model.addAttribute("authError", e.getMessage());
            return "register";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        authService.logout(session);
        return "redirect:/login";
    }
}
