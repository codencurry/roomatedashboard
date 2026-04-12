package com.tammam.roomatedash.service;

import com.tammam.roomatedash.dto.ChoreForm;
import com.tammam.roomatedash.exception.ResourceNotFoundException;
import com.tammam.roomatedash.model.Chore;
import com.tammam.roomatedash.model.ChoreStatus;
import com.tammam.roomatedash.model.Household;
import com.tammam.roomatedash.model.Roommate;
import com.tammam.roomatedash.repo.ChoreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
public class ChoreService {

    private final RoommateService roommateService;
    private final ChoreRepository choreRepo;
    private final HouseholdContext householdContext;
    private final HouseholdService householdService;
    private final ActivityLogService activityLogService;

    public ChoreService(RoommateService roommateService,
                        ChoreRepository choreRepo,
                        HouseholdContext householdContext,
                        HouseholdService householdService,
                        ActivityLogService activityLogService) {
        this.roommateService = roommateService;
        this.choreRepo = choreRepo;
        this.householdContext = householdContext;
        this.householdService = householdService;
        this.activityLogService = activityLogService;
    }

    @Transactional
    public void addChore(ChoreForm form) {
        Roommate assignedTo = roommateService.getRoommate(form.getAssignedToId(), "Assigned roommate not found.");
        String description = form.getDescription() == null || form.getDescription().trim().isEmpty()
                ? null
                : form.getDescription().trim();
        Long householdId = householdContext.getCurrentHouseholdId().orElse(null);
        Household household = householdId == null ? null : householdService.findHousehold(householdId);
        Chore chore = new Chore(form.getTitle().trim(), description, assignedTo, form.getDueDate(), household);
        chore.setRotating(form.isRotating());
        choreRepo.save(chore);
        activityLogService.log("CHORE_ADDED", "Assigned chore " + form.getTitle().trim() + " to " + assignedTo.getName() + ".");
    }

    @Transactional
    public void completeChore(Long id) {
        Chore chore = choreRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chore not found."));
        chore.complete();
        activityLogService.log("CHORE_COMPLETED", chore.getAssignedTo().getName() + " completed " + chore.getTitle() + ".");
    }

    @Transactional
    public void rotateWeeklyChores() {
        List<Roommate> roommates = roommateService.getRoommates();
        if (roommates.isEmpty()) {
            return;
        }

        for (Chore chore : getChores()) {
            if (!chore.isRotating()) {
                continue;
            }

            int currentIndex = -1;
            for (int i = 0; i < roommates.size(); i++) {
                if (roommates.get(i).getId().equals(chore.getAssignedTo().getId())) {
                    currentIndex = i;
                    break;
                }
            }
            int nextIndex = currentIndex < 0 ? 0 : (currentIndex + 1) % roommates.size();
            chore.setAssignedTo(roommates.get(nextIndex));
            chore.setStatus(ChoreStatus.OPEN);
            chore.setCompletedAt(null);
            chore.setDueDate(chore.getDueDate() == null ? java.time.LocalDate.now().plusDays(7) : chore.getDueDate().plusDays(7));
        }
        activityLogService.log("CHORES_ROTATED", "Rotated weekly chores.");
    }

    @Transactional(readOnly = true)
    public List<Chore> getChores() {
        List<Chore> chores = householdContext.getCurrentHouseholdId()
                .map(choreRepo::findByHouseholdId)
                .orElseGet(choreRepo::findAll);
        chores.sort(Comparator
                .comparing((Chore chore) -> chore.getStatus() == ChoreStatus.COMPLETED)
                .thenComparing(chore -> chore.getDueDate() == null)
                .thenComparing(Chore::getDueDate, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(Chore::getCreatedAt, Comparator.reverseOrder()));
        return chores;
    }
}
