package com.tammam.roomatedash.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

@Component
public class HouseholdContext {

    public Optional<Long> getCurrentHouseholdId() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return Optional.empty();
        }

        HttpServletRequest request = attributes.getRequest();
        HttpSession session = request.getSession(false);
        if (session == null) {
            return Optional.empty();
        }

        Object householdId = session.getAttribute(MockAuthService.SESSION_HOUSEHOLD_ID);
        return householdId instanceof Long id ? Optional.of(id) : Optional.empty();
    }
}
