package com.arenapulse;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VenueRepository extends JpaRepository<Venue, Integer> {

    Page<Venue> findBySportIgnoreCase(String sport, Pageable pageable);

    Page<Venue> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Venue> findBySportIgnoreCaseAndNameContainingIgnoreCase(
        String sport, String name, Pageable pageable);
}
