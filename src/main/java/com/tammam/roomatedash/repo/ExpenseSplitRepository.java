package com.tammam.roomatedash.repo;

import com.tammam.roomatedash.model.ExpenseSplit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpenseSplitRepository extends JpaRepository<ExpenseSplit, Long> {
    List<ExpenseSplit> findByExpenseIdOrderByRoommate_NameAsc(Long expenseId);
    long countByExpenseHouseholdIdAndPaidFalse(Long householdId);
}
