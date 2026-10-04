package com.finshield.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finshield.dto.request.LoginRequest;
import com.finshield.dto.request.RegisterRequest;
import com.finshield.entity.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * End-to-end tests for the authentication flow, run against an actual
 * (test-profile) Spring context and embedded MVC dispatcher.
 *
 * NOTE: Run with a test-specific datasource (e.g. H2 in-memory) rather than
 * your real MySQL instance — add an application-test.yml with an H2 URL and
 * activate the "test" profile, or point DB_URL/DB_USERNAME/DB_PASSWORD env
 * vars at a disposable schema.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void register_thenLogin_returnsJwtBothTimes() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setName("Integration Test User");
        registerRequest.setEmail("it-user@finshield.com");
        registerRequest.setPassword("Password@123");
        registerRequest.setRole(Role.CUSTOMER);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.email").value("it-user@finshield.com"))
                .andExpect(jsonPath("$.role").value("CUSTOMER"));

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("it-user@finshield.com");
        loginRequest.setPassword("Password@123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void login_withWrongPassword_returnsUnauthorized() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setName("Another User");
        registerRequest.setEmail("wrong-pass@finshield.com");
        registerRequest.setPassword("CorrectPassword@1");
        registerRequest.setRole(Role.CUSTOMER);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        LoginRequest badLogin = new LoginRequest();
        badLogin.setEmail("wrong-pass@finshield.com");
        badLogin.setPassword("WrongPassword@1");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badLogin)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void register_withDuplicateEmail_isRejected() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setName("Duplicate User");
        registerRequest.setEmail("duplicate@finshield.com");
        registerRequest.setPassword("Password@123");
        registerRequest.setRole(Role.CUSTOMER);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isForbidden()); // UnauthorizedActionException -> 403 in GlobalExceptionHandler
    }
}
