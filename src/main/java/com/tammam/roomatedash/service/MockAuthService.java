package com.tammam.roomatedash.service;

import com.tammam.roomatedash.dto.LoginForm;
import com.tammam.roomatedash.dto.RegisterForm;
import com.tammam.roomatedash.exception.DashboardException;
import com.tammam.roomatedash.model.AppUser;
import com.tammam.roomatedash.repo.AppUserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class MockAuthService {

    public static final String SESSION_USER_ID = "mockUserId";
    public static final String SESSION_HOUSEHOLD_ID = "mockHouseholdId";

    private final AppUserRepository appUserRepo;

    public MockAuthService(AppUserRepository appUserRepo) {
        this.appUserRepo = appUserRepo;
    }

    @Transactional
    public AppUser register(RegisterForm form) {
        String email = normalizeEmail(form.getEmail());
        appUserRepo.findByEmailIgnoreCase(email).ifPresent(user -> {
            throw new DashboardException("An account with that email already exists.");
        });

        return appUserRepo.save(new AppUser(form.getName().trim(), email));
    }

    @Transactional(readOnly = true)
    public AppUser login(LoginForm form) {
        return appUserRepo.findByEmailIgnoreCase(normalizeEmail(form.getEmail()))
                .orElseThrow(() -> new DashboardException("No mock account exists for that email."));
    }

    public void storeLogin(HttpSession session, AppUser user) {
        session.setAttribute(SESSION_USER_ID, user.getId());
    }

    public void logout(HttpSession session) {
        session.invalidate();
    }

    public void storeCurrentHousehold(HttpSession session, Long householdId) {
        session.setAttribute(SESSION_HOUSEHOLD_ID, householdId);
    }

    public Optional<Long> getCurrentHouseholdId(HttpSession session) {
        Object householdId = session.getAttribute(SESSION_HOUSEHOLD_ID);
        if (!(householdId instanceof Long id)) {
            return Optional.empty();
        }
        return Optional.of(id);
    }

    @Transactional(readOnly = true)
    public Optional<AppUser> getCurrentUser(HttpSession session) {
        Object userId = session.getAttribute(SESSION_USER_ID);
        if (!(userId instanceof Long id)) {
            return Optional.empty();
        }

        return appUserRepo.findById(id);
    }

    public boolean isLoggedIn(HttpSession session) {
        return session.getAttribute(SESSION_USER_ID) instanceof Long;
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }
}
