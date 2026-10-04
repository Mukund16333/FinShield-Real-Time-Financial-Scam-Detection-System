package com.finshield;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class FinShieldApplication {
    public static void main(String[] args) {
        SpringApplication.run(FinShieldApplication.class, args);
    }
}
