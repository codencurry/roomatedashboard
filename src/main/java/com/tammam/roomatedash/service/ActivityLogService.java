package com.tammam.roomatedash.service;

import com.tammam.roomatedash.model.ActivityLog;
import com.tammam.roomatedash.model.AppUser;
import com.tammam.roomatedash.model.Household;
import com.tammam.roomatedash.repo.ActivityLogRepository;
import com.tammam.roomatedash.repo.HouseholdRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Comparator;
import java.util.List;

@Service
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepo;
    private final HouseholdContext householdContext;
    private final HouseholdRepository householdRepo;
    private final MockAuthService authService;

    public ActivityLogService(ActivityLogRepository activityLogRepo,
                              HouseholdContext householdContext,
                              HouseholdRepository householdRepo,
                              MockAuthService authService) {
        this.activityLogRepo = activityLogRepo;
        this.householdContext = householdContext;
        this.householdRepo = householdRepo;
        this.authService = authService;
    }

    @Transactional
    public void log(String actionType, String message) {
        Household household = householdContext.getCurrentHouseholdId()
                .flatMap(householdRepo::findById)
                .orElse(null);
        AppUser actor = currentSession()
                .flatMap(authService::getCurrentUser)
                .orElse(null);
        activityLogRepo.save(new ActivityLog(household, actor, actionType, message));
    }

    @Transactional(readOnly = true)
    public List<ActivityLog> getRecentActivity() {
        return householdContext.getCurrentHouseholdId()
                .map(activityLogRepo::findTop12ByHouseholdIdOrderByCreatedAtDesc)
                .orElseGet(() -> {
                    List<ActivityLog> logs = activityLogRepo.findAll();
                    logs.sort(Comparator.comparing(ActivityLog::getCreatedAt).reversed());
                    return logs.stream().limit(12).toList();
                });
    }

    private java.util.Optional<HttpSession> currentSession() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return java.util.Optional.empty();
        }

        HttpServletRequest request = attributes.getRequest();
        return java.util.Optional.ofNullable(request.getSession(false));
    }
}
