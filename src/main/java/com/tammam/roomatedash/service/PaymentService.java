package com.tammam.roomatedash.service;

import com.tammam.roomatedash.dto.PaymentForm;
import com.tammam.roomatedash.exception.DashboardException;
import com.tammam.roomatedash.model.Household;
import com.tammam.roomatedash.model.Payment;
import com.tammam.roomatedash.model.Roommate;
import com.tammam.roomatedash.repo.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
public class PaymentService {

    private final RoommateService roommateService;
    private final PaymentRepository paymentRepo;
    private final HouseholdContext householdContext;
    private final HouseholdService householdService;
    private final ActivityLogService activityLogService;

    public PaymentService(RoommateService roommateService,
                          PaymentRepository paymentRepo,
                          HouseholdContext householdContext,
                          HouseholdService householdService,
                          ActivityLogService activityLogService) {
        this.roommateService = roommateService;
        this.paymentRepo = paymentRepo;
        this.householdContext = householdContext;
        this.householdService = householdService;
        this.activityLogService = activityLogService;
    }

    @Transactional
    public void recordPayment(PaymentForm form) {
        if (form.getFromRoommateId().equals(form.getToRoommateId())) {
            throw new DashboardException("From and To cannot be the same person.");
        }

        Roommate from = roommateService.getRoommate(form.getFromRoommateId(), "From roommate not found.");
        Roommate to = roommateService.getRoommate(form.getToRoommateId(), "To roommate not found.");

        String note = form.getNote() == null || form.getNote().trim().isEmpty() ? null : form.getNote().trim();
        Long householdId = householdContext.getCurrentHouseholdId().orElse(null);
        Household household = householdId == null ? null : householdService.findHousehold(householdId);
        paymentRepo.save(new Payment(from, to, form.getAmount(), note, household));
        activityLogService.log("PAYMENT_RECORDED", from.getName() + " paid " + to.getName() + " $" + form.getAmount() + ".");
    }

    @Transactional(readOnly = true)
    public List<Payment> getPayments() {
        List<Payment> list = householdContext.getCurrentHouseholdId()
                .map(paymentRepo::findByHouseholdId)
                .orElseGet(paymentRepo::findAll);
        list.sort(Comparator.comparing(Payment::getCreatedAt).reversed());
        return list;
    }
}
