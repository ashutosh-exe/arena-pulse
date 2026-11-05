package com.arenapulse;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VenueRepository extends JpaRepository<Venue, Integer> {

    List<Venue> findBySportIgnoreCase(String sport);

    List<Venue> findByNameContainingIgnoreCase(String name);

    List<Venue> findBySportIgnoreCaseAndNameContainingIgnoreCase(
        String sport, String name);
}
