package com.finshield.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finshield.dto.request.LoginRequest;
import com.finshield.dto.request.RegisterRequest;
import com.finshield.dto.request.TransactionRequest;
import com.finshield.dto.response.JwtResponse;
import com.finshield.entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * End-to-end tests covering transaction submission through the fraud
 * scoring pipeline. Requires a "test" profile datasource (see AuthControllerIT).
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class TransactionControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String customerToken;

    @BeforeEach
    void authenticateCustomer() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setName("Txn Test Customer");
        registerRequest.setEmail("txn-customer@finshield.com");
        registerRequest.setPassword("Password@123");
        registerRequest.setRole(Role.CUSTOMER);

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andReturn();

        JwtResponse jwtResponse = objectMapper.readValue(
                result.getResponse().getContentAsString(), JwtResponse.class);
        customerToken = jwtResponse.getToken();
    }

    @Test
    void submit_cleanTransaction_returnsSuccessStatus() throws Exception {
        TransactionRequest request = new TransactionRequest();
        request.setSenderAccountId("ACC-CLEAN-1");
        request.setReceiverAccountId("ACC-CLEAN-2");
        request.setAmount(BigDecimal.valueOf(500));

        mockMvc.perform(post("/api/transactions")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.riskScore").value(0));
    }

    @Test
    void submit_largeAmountTransaction_getsFlaggedOrBlocked() throws Exception {
        TransactionRequest request = new TransactionRequest();
        request.setSenderAccountId("ACC-HIGH-1");
        request.setReceiverAccountId("ACC-HIGH-2");
        request.setAmount(BigDecimal.valueOf(75000)); // above default 50,000 threshold

        mockMvc.perform(post("/api/transactions")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(org.hamcrest.Matchers.not("SUCCESS")))
                .andExpect(jsonPath("$.triggeredRules", org.hamcrest.Matchers.hasItem("AMOUNT_THRESHOLD")));
    }

    @Test
    void submit_withoutToken_isUnauthorized() throws Exception {
        TransactionRequest request = new TransactionRequest();
        request.setSenderAccountId("ACC-NOAUTH-1");
        request.setReceiverAccountId("ACC-NOAUTH-2");
        request.setAmount(BigDecimal.valueOf(100));

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void submit_invalidAmount_returnsBadRequest() throws Exception {
        TransactionRequest request = new TransactionRequest();
        request.setSenderAccountId("ACC1001");
        request.setReceiverAccountId("ACC2002");
        request.setAmount(BigDecimal.ZERO); // fails @DecimalMin("0.01")

        mockMvc.perform(post("/api/transactions")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getById_returnsNotFound_forUnknownTransaction() throws Exception {
        mockMvc.perform(get("/api/transactions/999999")
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isNotFound());
    }
}
