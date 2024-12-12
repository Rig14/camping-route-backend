package ee.taltech.iti03022024backend.controller;

import ee.taltech.iti03022024backend.AbstractIntegrationTest;
import ee.taltech.iti03022024backend.dto.UserDto;
import ee.taltech.iti03022024backend.dto.VerificationDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void givenValidUserDto_whenCreateUser_thenReturnsOkAndVerificationDto() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setUsername("newuser");
        userDto.setEmail("newuser@example.com");
        userDto.setPassword("StrongPass123!");

        mvc.perform(post("/api/public/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.userId").isNumber());
    }

    @Test
    void givenExistingUsername_whenCreateUser_thenReturnsConflict() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setUsername("existinguser");
        userDto.setEmail("existinguser@example.com");
        userDto.setPassword("StrongPass123!");

        mvc.perform(post("/api/public/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk());

        mvc.perform(post("/api/public/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Username is already taken"));
    }

    @Test
    void givenValidCredentials_whenVerifyUser_thenReturnsOkAndVerificationDto() throws Exception {
        UserDto createDto = new UserDto();
        createDto.setUsername("verifyuser");
        createDto.setEmail("verifyuser@example.com");
        createDto.setPassword("StrongPass123!");

        mvc.perform(post("/api/public/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk());

        UserDto verifyDto = new UserDto();
        verifyDto.setUsername("verifyuser");
        verifyDto.setPassword("StrongPass123!");

        mvc.perform(post("/api/public/user/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verifyDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.userId").isNumber());
    }

    @Test
    void givenExistingUserId_whenGetUser_thenReturnsOkAndUserDto() throws Exception {
        UserDto createDto = new UserDto();
        createDto.setUsername("getuser");
        createDto.setEmail("getuser@example.com");
        createDto.setPassword("StrongPass123!");

        String createResponse = mvc.perform(post("/api/public/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        VerificationDto verificationDto = objectMapper.readValue(createResponse, VerificationDto.class);
        long userId = verificationDto.getUserId();

        mvc.perform(get("/api/public/user/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("getuser"))
                .andExpect(jsonPath("$.email").value("getuser@example.com"));
    }

    @Test
    @WithMockUser(username = "deleteuser")
    void givenValidUser_whenDeleteUser_thenReturnsNoContent() throws Exception {
        UserDto createDto = new UserDto();
        createDto.setUsername("deleteuser");
        createDto.setEmail("deleteuser@example.com");
        createDto.setPassword("StrongPass123!");

        String createResponse = mvc.perform(post("/api/public/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        VerificationDto verificationDto = objectMapper.readValue(createResponse, VerificationDto.class);
        long userId = verificationDto.getUserId();

        mvc.perform(delete("/api/user/{id}", userId))
                .andExpect(status().isNoContent());

        mvc.perform(get("/api/public/user/{id}", userId))
                .andExpect(status().isNotFound());
    }
}
