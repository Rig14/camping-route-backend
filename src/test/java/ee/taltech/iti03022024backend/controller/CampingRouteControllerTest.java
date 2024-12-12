package ee.taltech.iti03022024backend.controller;

import ee.taltech.iti03022024backend.AbstractIntegrationTest;
import ee.taltech.iti03022024backend.dto.CampingRouteDto;
import ee.taltech.iti03022024backend.dto.CampingRouteSearchRequest;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CampingRouteControllerTest extends AbstractIntegrationTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void givenCampingRouteDto_whenCreateCampingRouteAndUserLoggedIn_thenReturnsOkAndCorrectData() throws Exception {
        CampingRouteDto campingRouteDto = new CampingRouteDto();
        campingRouteDto.setName("TestTestTest");
        campingRouteDto.setDescription("TestTestTest");
        campingRouteDto.setLocation("TestTestTest");

        String requestBody = objectMapper.writeValueAsString(campingRouteDto);

        mvc.perform(post("/api/camping_routes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(user("user1")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("TestTestTest"))
                .andExpect(jsonPath("$.description").value("TestTestTest"))
                .andExpect(jsonPath("$.location").value("TestTestTest"));
    }

    @Test
    void givenCampingRouteSearchRequest_whenSearchCampingRoutes_thenReturnsOkAndCorrectData() throws Exception {
        CampingRouteSearchRequest searchRequest = new CampingRouteSearchRequest();
        searchRequest.setKeyword("2");
        searchRequest.setPageNumber(0);
        searchRequest.setPageSize(5);

        String requestBody = objectMapper.writeValueAsString(searchRequest);

        mvc.perform(post("/api/public/camping_routes/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void givenCampingRouteSearchRequest_whenGetCampingRoutes_thenReturnsOkAndCorrectData() throws Exception {
        CampingRouteSearchRequest searchRequest = new CampingRouteSearchRequest();
        searchRequest.setKeyword("");
        searchRequest.setPageNumber(0);
        searchRequest.setPageSize(5);

        String requestBody = objectMapper.writeValueAsString(searchRequest);

        mvc.perform(post("/api/public/camping_routes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void givenExistingCampingRoute_whenGetCampingRoute_thenReturnsOkAndCorrectData() throws Exception {
        mvc.perform(get("/api/public/camping_routes/{id}", 2L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.name").value("Test Route"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.location").value("Test Location"));
    }

    @Test
    void givenNonExistingCampingRoute_whenGetCampingRoute_thenReturnsNotFound() throws Exception {
        mvc.perform(get("/api/public/camping_routes/{id}", 999L))
                .andExpect(status().isNotFound());
    }
}
