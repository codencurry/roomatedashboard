package com.tammam.roomatedash.service;

import com.tammam.roomatedash.dto.HouseholdForm;
import com.tammam.roomatedash.dto.JoinHouseholdForm;
import com.tammam.roomatedash.exception.DashboardException;
import com.tammam.roomatedash.exception.ResourceNotFoundException;
import com.tammam.roomatedash.model.AppUser;
import com.tammam.roomatedash.model.Household;
import com.tammam.roomatedash.model.HouseholdMember;
import com.tammam.roomatedash.model.HouseholdRole;
import com.tammam.roomatedash.repo.HouseholdMemberRepository;
import com.tammam.roomatedash.repo.HouseholdRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;

@Service
public class HouseholdService {

    private static final String INVITE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

    private final HouseholdRepository householdRepo;
    private final HouseholdMemberRepository memberRepo;
    private final MockAuthService authService;
    private final ActivityLogService activityLogService;
    private final SecureRandom random = new SecureRandom();

    public HouseholdService(HouseholdRepository householdRepo,
                            HouseholdMemberRepository memberRepo,
                            MockAuthService authService,
                            ActivityLogService activityLogService) {
        this.householdRepo = householdRepo;
        this.memberRepo = memberRepo;
        this.authService = authService;
        this.activityLogService = activityLogService;
    }

    @Transactional
    public Household createHousehold(HouseholdForm form, HttpSession session) {
        AppUser user = authService.getCurrentUser(session)
                .orElseThrow(() -> new DashboardException("Log in before creating a household."));
        Household household = householdRepo.save(new Household(form.getName().trim(), generateInviteCode(), user));
        memberRepo.save(new HouseholdMember(household, user, HouseholdRole.ORGANIZER));
        authService.storeCurrentHousehold(session, household.getId());
        activityLogService.log("HOUSEHOLD_CREATED", user.getName() + " created household " + household.getName() + ".");
        return household;
    }

    @Transactional
    public Household joinHousehold(JoinHouseholdForm form, HttpSession session) {
        AppUser user = authService.getCurrentUser(session)
                .orElseThrow(() -> new DashboardException("Log in before joining a household."));
        Household household = householdRepo.findByInviteCodeIgnoreCase(form.getInviteCode().trim())
                .orElseThrow(() -> new ResourceNotFoundException("Household invite code not found."));

        if (!memberRepo.existsByHouseholdIdAndUserId(household.getId(), user.getId())) {
            memberRepo.save(new HouseholdMember(household, user, HouseholdRole.MEMBER));
            authService.storeCurrentHousehold(session, household.getId());
            activityLogService.log("HOUSEHOLD_JOINED", user.getName() + " joined household " + household.getName() + ".");
        }

        authService.storeCurrentHousehold(session, household.getId());
        return household;
    }

    @Transactional(readOnly = true)
    public Household findHousehold(Long id) {
        return householdRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Household not found."));
    }

    @Transactional(readOnly = true)
    public List<HouseholdMember> getMemberships(HttpSession session) {
        return authService.getCurrentUser(session)
                .map(user -> memberRepo.findByUserIdOrderByJoinedAtAsc(user.getId()))
                .orElse(List.of());
    }

    @Transactional(readOnly = true)
    public Household getCurrentHousehold(HttpSession session) {
        Long householdId = authService.getCurrentHouseholdId(session)
                .orElseThrow(() -> new DashboardException("Choose a household first."));
        return householdRepo.findById(householdId)
                .orElseThrow(() -> new ResourceNotFoundException("Household not found."));
    }

    @Transactional(readOnly = true)
    public boolean userHasHousehold(HttpSession session) {
        return !getMemberships(session).isEmpty();
    }

    @Transactional(readOnly = true)
    public boolean userCanAccessCurrentHousehold(HttpSession session) {
        return authService.getCurrentUser(session)
                .flatMap(user -> authService.getCurrentHouseholdId(session)
                        .map(householdId -> memberRepo.existsByHouseholdIdAndUserId(householdId, user.getId())))
                .orElse(false);
    }

    @Transactional(readOnly = true)
    public boolean ensureCurrentHousehold(HttpSession session) {
        if (userCanAccessCurrentHousehold(session)) {
            return true;
        }

        List<HouseholdMember> memberships = getMemberships(session);
        if (memberships.isEmpty()) {
            return false;
        }

        authService.storeCurrentHousehold(session, memberships.get(0).getHousehold().getId());
        return true;
    }

    @Transactional
    public Household ensureOrCreateDefaultHousehold(HttpSession session) {
        if (userCanAccessCurrentHousehold(session)) {
            return getCurrentHousehold(session);
        }

        AppUser user = authService.getCurrentUser(session)
                .orElseThrow(() -> new DashboardException("Log in before opening the dashboard."));

        List<HouseholdMember> memberships = memberRepo.findByUserIdOrderByJoinedAtAsc(user.getId());
        if (!memberships.isEmpty()) {
            Household household = memberships.get(0).getHousehold();
            authService.storeCurrentHousehold(session, household.getId());
            return household;
        }

        List<Household> createdHouseholds = householdRepo.findByCreatedByIdOrderByCreatedAtAsc(user.getId());
        if (!createdHouseholds.isEmpty()) {
            Household household = createdHouseholds.get(0);
            if (!memberRepo.existsByHouseholdIdAndUserId(household.getId(), user.getId())) {
                memberRepo.save(new HouseholdMember(household, user, HouseholdRole.ORGANIZER));
            }
            authService.storeCurrentHousehold(session, household.getId());
            return household;
        }

        Household household = householdRepo.save(new Household(user.getName() + "'s Household", generateInviteCode(), user));
        memberRepo.save(new HouseholdMember(household, user, HouseholdRole.ORGANIZER));
        authService.storeCurrentHousehold(session, household.getId());
        activityLogService.log("HOUSEHOLD_CREATED", "Created default household for " + user.getName() + ".");
        return household;
    }

    public void switchHousehold(Long householdId, HttpSession session) {
        AppUser user = authService.getCurrentUser(session)
                .orElseThrow(() -> new DashboardException("Log in before switching households."));
        if (!memberRepo.existsByHouseholdIdAndUserId(householdId, user.getId())) {
            throw new DashboardException("You are not a member of that household.");
        }
        authService.storeCurrentHousehold(session, householdId);
    }

    private String generateInviteCode() {
        String code;
        do {
            StringBuilder out = new StringBuilder();
            for (int i = 0; i < 8; i++) {
                out.append(INVITE_CHARS.charAt(random.nextInt(INVITE_CHARS.length())));
            }
            code = out.toString();
        } while (householdRepo.existsByInviteCodeIgnoreCase(code));

        return code;
    }
}
