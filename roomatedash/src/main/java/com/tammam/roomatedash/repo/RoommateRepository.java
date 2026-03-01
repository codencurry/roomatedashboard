package com.tammam.roomatedash.repo;

import com.tammam.roomatedash.model.Roommate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoommateRepository extends JpaRepository<Roommate, Long> {
    Optional<Roommate> findByNameIgnoreCase(String name);
}
