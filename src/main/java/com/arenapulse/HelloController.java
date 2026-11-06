
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

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
    public Page<Venue> getAllVenues(
        @RequestParam(required = false) String sport,
        @RequestParam(required = false) String name,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "name") String sortBy,
        @RequestParam(defaultValue = "asc") String direction) { {

        if (page < 0 || size <= 0 || size > 100) {
        throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST,
            "Page must be non-negative and size must be between 1 and 100"
        );
    }

    if (!sortBy.equals("name") && !sortBy.equals("sport") && !sortBy.equals("id")) {
        throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST,
            "Invalid sort field"
        );
    }

    if (!direction.equalsIgnoreCase("asc") && !direction.equalsIgnoreCase("desc")) {
        throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST,
            "Direction must be asc or desc"
        );
    }

    Sort sort = Sort.by(
        direction.equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC,
        sortBy
    );

    Pageable pageable = PageRequest.of(page, size, sort);

    if (sport != null && name != null) {
        return venueRepository.findBySportIgnoreCaseAndNameContainingIgnoreCase(
            sport, name, pageable
        );
    }

    if (sport != null) {
         return venueRepository.findBySportIgnoreCase(sport, pageable);
    }

    if (name != null) {
        return venueRepository.findByNameContainingIgnoreCase(name, pageable);
    }

    return venueRepository.findAll(pageable);
        }
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
