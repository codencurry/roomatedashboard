package com.tammam.roomatedash.service;

import com.tammam.roomatedash.dto.RoommateForm;
import com.tammam.roomatedash.exception.DashboardException;
import com.tammam.roomatedash.exception.ResourceNotFoundException;
import com.tammam.roomatedash.model.Roommate;
import com.tammam.roomatedash.repo.RoommateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
public class RoommateService {

    private final RoommateRepository roommateRepo;
    private final HouseholdContext householdContext;
    private final HouseholdService householdService;
    private final ActivityLogService activityLogService;

    public RoommateService(RoommateRepository roommateRepo,
                           HouseholdContext householdContext,
                           HouseholdService householdService,
                           ActivityLogService activityLogService) {
        this.roommateRepo = roommateRepo;
        this.householdContext = householdContext;
        this.householdService = householdService;
        this.activityLogService = activityLogService;
    }

    @Transactional
    public void addRoommate(RoommateForm form) {
        String trimmed = form.getName().trim();

        Long householdId = householdContext.getCurrentHouseholdId().orElse(null);
        if (householdId == null) {
            roommateRepo.findByNameIgnoreCase(trimmed).ifPresent(r -> {
                throw new DashboardException("Roommate already exists.");
            });

            roommateRepo.save(new Roommate(trimmed));
            activityLogService.log("ROOMMATE_ADDED", "Added roommate " + trimmed + ".");
            return;
        }

        roommateRepo.findByHouseholdIdAndNameIgnoreCase(householdId, trimmed).ifPresent(r -> {
            throw new DashboardException("Roommate already exists in this household.");
        });

        roommateRepo.save(new Roommate(trimmed, householdService.findHousehold(householdId)));
        activityLogService.log("ROOMMATE_ADDED", "Added roommate " + trimmed + ".");
    }

    @Transactional(readOnly = true)
    public Roommate getRoommate(Long id, String message) {
        Roommate roommate = roommateRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(message));
        householdContext.getCurrentHouseholdId().ifPresent(householdId -> {
            if (roommate.getHousehold() == null || !roommate.getHousehold().getId().equals(householdId)) {
                throw new ResourceNotFoundException(message);
            }
        });
        return roommate;
    }

    @Transactional(readOnly = true)
    public List<Roommate> getRoommates() {
        List<Roommate> list = householdContext.getCurrentHouseholdId()
                .map(roommateRepo::findByHouseholdId)
                .orElseGet(roommateRepo::findAll);
        list.sort(Comparator.comparing(Roommate::getName));
        return list;
    }
}
