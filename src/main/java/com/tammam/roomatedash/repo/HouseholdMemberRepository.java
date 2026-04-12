package com.tammam.roomatedash.repo;

import com.tammam.roomatedash.model.HouseholdMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HouseholdMemberRepository extends JpaRepository<HouseholdMember, Long> {
    List<HouseholdMember> findByUserIdOrderByJoinedAtAsc(Long userId);
    boolean existsByHouseholdIdAndUserId(Long householdId, Long userId);
    Optional<HouseholdMember> findByHouseholdIdAndUserId(Long householdId, Long userId);
}
