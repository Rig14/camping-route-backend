package ee.taltech.iti03022024backend.controller;

import ee.taltech.iti03022024backend.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CampingRouteControllerTest extends AbstractIntegrationTest {
    @Autowired
    private MockMvc mvc;

    @Test
    void givenExistingCampingRoute_whenGetCampingRoute_thenReturnsOkAndCorrectData() throws Exception {
        mvc.perform(get("/api/public/camping_routes/{id}", 1L)
                        .with(user("user")))
                .andExpect(status().isOk());
    }

    @Test
    void givenNonExistingCampingRoute_whenGetCampingRoute_thenReturnsNotFound() throws Exception {
        mvc.perform(get("/api/public/camping_routes/{id}", 999L)
                        .with(user("user")))
                .andExpect(status().isNotFound());
    }
}
