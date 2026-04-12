package com.tammam.roomatedash.repo;

import com.tammam.roomatedash.model.Chore;
import com.tammam.roomatedash.model.ChoreStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ChoreRepository extends JpaRepository<Chore, Long> {
    List<Chore> findByHouseholdId(Long householdId);
    long countByHouseholdIdAndStatusAndDueDateLessThan(Long householdId, ChoreStatus status, LocalDate dueDate);
}
