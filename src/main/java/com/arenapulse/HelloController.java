
package com.arenapulse;

import java.net.URI;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class HelloController {

    private final VenueRepository venueRepository;

    public HelloController(VenueRepository venueRepository) {
        this.venueRepository = venueRepository;
    }

    @GetMapping("/")
    public String home() {
        return "Welcome to ArenaPulse!";
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello from ArenaPulse API!";
    }

    
    @GetMapping("/api/venues")
    public List<Venue> getVenues(
        @RequestParam(required = false) String sport,
        @RequestParam(required = false) String name) {

        boolean hasSport = sport != null && !sport.isBlank();
        boolean hasName = name != null && !name.isBlank();

        if (hasSport && hasName) {
            return venueRepository
                .findBySportIgnoreCaseAndNameContainingIgnoreCase(
                        sport, name);
        }

        if (hasSport) {
        return venueRepository.findBySportIgnoreCase(sport);
        }

        if (hasName) {
        return venueRepository.findByNameContainingIgnoreCase(name);
        }

        return venueRepository.findAll();
    }

    @GetMapping("/api/venues/{id}")
    public Venue getVenueById(@PathVariable int id) {
        return venueRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Venue not found"));
    }
    
    
    @PostMapping("/api/venues")
    public ResponseEntity<Venue> createVenue(@Valid @RequestBody Venue venue) {
        Venue savedVenue = venueRepository.save(venue);

        URI location = URI.create("/api/venues/" + savedVenue.getId());

        return ResponseEntity
            .created(location)
            .body(savedVenue);
    }
    @PutMapping("/api/venues/{id}")
    public ResponseEntity<Venue> updateVenue(
            @PathVariable Integer id,
            @Valid @RequestBody Venue updatedVenue) {
                Venue existingVenue = venueRepository.findById(id)  
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Venue not found"
        ));
        existingVenue.setName(updatedVenue.getName());
        existingVenue.setSport(updatedVenue.getSport());

        Venue savedVenue = venueRepository.save(existingVenue);

        return ResponseEntity.ok(savedVenue);
    }
    @DeleteMapping("/api/venues/{id}")
    public ResponseEntity<Void> deleteVenue(@PathVariable Integer id) {

        Venue venue = venueRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                       HttpStatus.NOT_FOUND,
                      "Venue not found"
                ));

        venueRepository.delete(venue);

        return ResponseEntity.noContent().build();
    }

}
