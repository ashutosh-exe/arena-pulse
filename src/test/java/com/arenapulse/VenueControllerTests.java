package com.arenapulse;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.MediaType;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
                .andExpect(jsonPath("$.content[*].sport", everyItem(is("Badminton"))));
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
    @Test
    void updateVenueShouldReturnUpdatedVenue() throws Exception {

        // First, create a venue
        String venueJson = """
                {
                    "name": "Original Court",
                    "sport": "Basketball"
                }
                """;

        String response = mockMvc.perform(post("/api/venues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(venueJson))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extract the generated ID
        int id = com.jayway.jsonpath.JsonPath
                .read(response, "$.id");

        // Now update the venue
        String updatedJson = """
               {
                    "name": "Updated Court",
                    "sport": "Badminton"
                }
             """;

        mockMvc.perform(put("/api/venues/" + id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(updatedJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Updated Court"))
            .andExpect(jsonPath("$.sport").value("Badminton"));
    }
    @Test
    void deleteVenueShouldReturnNoContent() throws Exception {

        // First create a venue
        String response = mockMvc.perform(post("/api/venues")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                            {
                                "name": "Delete Test Court",
                                "sport": "Basketball"
                            }
                            """))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

        // Extract the generated ID
        Integer id = com.jayway.jsonpath.JsonPath.read(response, "$.id");

        // Now delete that venue
        mockMvc.perform(delete("/api/venues/" + id))
                .andExpect(status().isNoContent());
    }
    @Test
    void deleteNonExistentVenueShouldReturnNotFound() throws Exception {

        mockMvc.perform(delete("/api/venues/999999"))
            .andExpect(status().isNotFound());
    }
}
