package com.tammam.roomatedash.repo;

import com.tammam.roomatedash.model.GroceryItem;
import com.tammam.roomatedash.model.GroceryItemStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroceryItemRepository extends JpaRepository<GroceryItem, Long> {
    List<GroceryItem> findByHouseholdId(Long householdId);
    long countByHouseholdIdAndStatus(Long householdId, GroceryItemStatus status);
}
