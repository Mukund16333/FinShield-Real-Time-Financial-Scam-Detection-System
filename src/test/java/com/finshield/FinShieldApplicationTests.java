package com.finshield;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Sanity check that the full Spring application context loads successfully
 * (all beans wire up correctly). Runs against the "dev" profile config.
 */
@SpringBootTest
@ActiveProfiles("dev")
class FinShieldApplicationTests {

    @Test
    void contextLoads() {
        // If the application context fails to start, this test fails.
    }
}
