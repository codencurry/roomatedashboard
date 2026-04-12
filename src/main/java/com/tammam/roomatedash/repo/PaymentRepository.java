package com.tammam.roomatedash.repo;

import com.tammam.roomatedash.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByHouseholdId(Long householdId);
}
