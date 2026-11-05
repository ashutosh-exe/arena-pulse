package com.arenapulse;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.MediaType;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class VenueControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getVenuesShouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/venues"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(
                        org.springframework.http.MediaType.APPLICATION_JSON));
    }
    @Test
    void filterVenuesBySportShouldReturnMatchingVenues() throws Exception {

        // First, create a badminton venue
        mockMvc.perform(post("/api/venues")
                       .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Test Badminton Court\",\"sport\":\"Badminton\"}"))
                .andExpect(status().isCreated());

        // Now, search for badminton venues
        mockMvc.perform(get("/api/venues?sport=Badminton"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].sport", everyItem(is("Badminton"))));
    }
    @Test
    void getNonExistentVenueShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/venues/999999999"))
                .andExpect(status().isNotFound());
    }
    @Test
    void createVenueWithBlankNameShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/venues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"sport\":\"Basketball\"}"))
                .andExpect(status().isBadRequest());
    }
    @Test
    void createVenueWithBlankSportShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/venues")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content("{\"name\":\"Test Court\",\"sport\":\"\"}"))
                    .andExpect(status().isBadRequest());
    }
    @Test
    void createValidVenueShouldReturnCreated() throws Exception {
        mockMvc.perform(post("/api/venues")
                       .contentType(MediaType.APPLICATION_JSON)
                      .content("{\"name\":\"Automated Test Court\",\"sport\":\"Basketball\"}"))
                .andExpect(status().isCreated());
    }
    @Test
    void createValidVenueShouldReturnLocationHeader() throws Exception {
        mockMvc.perform(post("/api/venues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Location Header Test\",\"sport\":\"Basketball\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }
}
