package com.tammam.roomatedash.repo;

import com.tammam.roomatedash.model.Household;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HouseholdRepository extends JpaRepository<Household, Long> {
    Optional<Household> findByInviteCodeIgnoreCase(String inviteCode);
    boolean existsByInviteCodeIgnoreCase(String inviteCode);
    List<Household> findByCreatedByIdOrderByCreatedAtAsc(Long createdById);
}
