package ee.taltech.iti03022024backend.controller;

import ee.taltech.iti03022024backend.AbstractIntegrationTest;
import ee.taltech.iti03022024backend.dto.CampingRouteDto;
import ee.taltech.iti03022024backend.dto.CampingRouteSearchRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.stream.Stream;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CampingRouteControllerTest extends AbstractIntegrationTest {

    static Stream<Object[]> provideTestParameters() {
        return Stream.of(
                new Object[]{"/api/public/camping_routes/search", "2", 0, 5, 1, 1},
                new Object[]{"/api/public/camping_routes", "", 0, 5, 2, 1},
                new Object[]{"/api/public/camping_routes/user", "2", 0, 5, 1, 1}
        );
    }

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
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.name").value("TestTestTest"))
                .andExpect(jsonPath("$.description").value("TestTestTest"))
                .andExpect(jsonPath("$.location").value("TestTestTest"));
    }

    @ParameterizedTest
    @MethodSource("provideTestParameters")
    void givenCampingRouteSearchRequest_whenDifferentActions_thenReturnsOkAndCorrectData(String url, String keyword, int pageNumber, int pageSize, int expectedTotalElements, int expectedTotalPages) throws Exception {
        CampingRouteSearchRequest searchRequest = new CampingRouteSearchRequest();
        searchRequest.setKeyword(keyword);
        searchRequest.setPageNumber(pageNumber);
        searchRequest.setPageSize(pageSize);

        String requestBody = objectMapper.writeValueAsString(searchRequest);

        mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(expectedTotalElements))
                .andExpect(jsonPath("$.totalPages").value(expectedTotalPages));
    }

    @Test
    void givenExistingCampingRoute_whenGetCampingRoute_thenReturnsOkAndCorrectData() throws Exception {
        mvc.perform(get("/api/public/camping_routes/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Route"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.location").value("Test Location"));
    }

    @Test
    void givenCampingRouteId_whenDeleteCampingRouteAndUserLoggedIn_thenReturnsNoContent() throws Exception {
        mvc.perform(delete("/api/camping_routes/{id}", 2L).with(user("user2")))
                .andExpect(status().isNoContent());
        mvc.perform(get("/api/public/camping_routes/{id}", 2L))
                .andExpect(status().isNotFound());
    }

}
