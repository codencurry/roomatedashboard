package com.tammam.roomatedash.config;

import com.tammam.roomatedash.service.MockAuthService;
import com.tammam.roomatedash.service.HouseholdService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class MockAuthInterceptor implements HandlerInterceptor {

    private final MockAuthService authService;
    private final HouseholdService householdService;

    public MockAuthInterceptor(MockAuthService authService, HouseholdService householdService) {
        this.authService = authService;
        this.householdService = householdService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (authService.isLoggedIn(request.getSession())) {
            String path = request.getRequestURI();
            if (!path.startsWith("/households") && !path.equals("/logout") && authService.getCurrentHouseholdId(request.getSession()).isEmpty()) {
                householdService.ensureOrCreateDefaultHousehold(request.getSession());
            }
            return true;
        }

        response.sendRedirect("/login");
        return false;
    }
}
