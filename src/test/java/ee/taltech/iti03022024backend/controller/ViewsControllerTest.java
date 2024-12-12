package ee.taltech.iti03022024backend.controller;

import ee.taltech.iti03022024backend.AbstractIntegrationTest;
import ee.taltech.iti03022024backend.repository.ViewRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ViewsControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ViewRepository viewRepository;

    @BeforeEach
    void clearViews() {
        viewRepository.deleteAll();
    }

    @Test
    @Transactional
    void givenValidCampingRouteId_whenAddView_thenReturnsOk() throws Exception {
        mvc.perform(post("/api/public/camping_routes/views/{campingRouteId}", 2L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.campingRouteId").value(2L))
                .andExpect(jsonPath("$.viewsCount").value(1));
    }

    @Test
    @Transactional
    void givenValidCampingRouteId_whenGetViewCount_thenReturnsOk() throws Exception {
        mvc.perform(get("/api/public/camping_routes/views/{campingRouteId}", 2L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.campingRouteId").value(2L))
                .andExpect(jsonPath("$.viewsCount").value(0));
    }

    @Test
    @Transactional
    void givenExistingCampingRoute_whenMultipleViewsAdded_thenViewCountIncreases() throws Exception {
        long campingRouteId = 2L;

        mvc.perform(post("/api/public/camping_routes/views/{campingRouteId}", campingRouteId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.viewsCount").value(1));

        mvc.perform(post("/api/public/camping_routes/views/{campingRouteId}", campingRouteId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.viewsCount").value(2));

        mvc.perform(get("/api/public/camping_routes/views/{campingRouteId}", campingRouteId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.viewsCount").value(2));
    }
}