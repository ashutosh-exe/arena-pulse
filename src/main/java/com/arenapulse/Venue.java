
package com.arenapulse;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Venue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Venue name cannot be blank")
    @Size(max = 100, message = "Venue name cannot exceed 100 characters")
    private String name;

    @NotBlank(message = "Sport cannot be blank")
    @Size(max = 50, message = "Sport cannot exceed 50 characters")
    private String sport;

    // Required by JPA
    public Venue() {
    }
    public Venue(String name, String sport) {
        this.name = name;
        this.sport = sport;
    }

    public Venue(int id, String name, String sport) {
        this.id = id;
        this.name = name;
        this.sport = sport;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSport() {
        return sport;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSport(String sport) {
        this.sport = sport;
    }
}
