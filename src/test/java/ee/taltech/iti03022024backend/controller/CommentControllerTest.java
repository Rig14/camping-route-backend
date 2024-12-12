package ee.taltech.iti03022024backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.taltech.iti03022024backend.AbstractIntegrationTest;
import ee.taltech.iti03022024backend.dto.CommentDto;
import org.junit.jupiter.api.Test;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CommentControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @Transactional
    void givenValidData_whenCreateComment_thenReturnsOk() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setContent("Great camping route!");

        String requestBody = objectMapper.writeValueAsString(commentDto);

        mvc.perform(post("/api/camping_routes/comments/{campingRouteId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(user("user1")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Great camping route!"));
    }

    @Test
    @Transactional
    void givenCampingRouteId_whenGetCommentsByCampingRoute_thenReturnsOk() throws Exception {
        mvc.perform(get("/api/public/camping_routes/comments/{campingRouteId}", 2L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].content").value("Test Comment"));
    }

    @Test
    @Transactional
    void givenUserId_whenGetCommentsByUserId_thenReturnsOk() throws Exception {
        mvc.perform(get("/api/public/camping_routes/comments/user/{userId}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].content").value("Test Comment"));
    }

    @Test
    @Transactional
    void givenCommentId_whenDeleteComment_thenReturnsNoContent() throws Exception {
        mvc.perform(delete("/api/camping_routes/comments/single/{commentId}", 1L)
                        .with(user("user1")))
                .andExpect(status().isNoContent());

        mvc.perform(get("/api/public/camping_routes/comments/{campingRouteId}", 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
