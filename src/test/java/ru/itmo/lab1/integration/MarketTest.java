package ru.itmo.lab1.integration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class MarketTest {

    @Container
    private static final PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:13.1-alpine")
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    public static void setUp() {
        container.start();
    }

    @AfterAll
    public static void tearDown() {
        container.stop();
    }

    @DynamicPropertySource
    public static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", container::getJdbcUrl);
        registry.add("spring.datasource.username", container::getUsername);
        registry.add("spring.datasource.password", container::getPassword);
        registry.add("spring.liquibase.changelog", () -> "classpath:db/changelog/db.changelog-master-test.xml");
    }

    @Test
    public void createMarket() throws Exception {
        String jwt = Utils.getToken(mockMvc, "test_maintainer", "test");

        mockMvc.perform(post("/market")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt)
                        .content("{ \"mic\": \"XNAS\", \"location\": \"New York\" }")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.mic").value("XNAS"))
                .andExpect(jsonPath("$.location").value("New York"));
    }

    @Test
    public void createMarketMICAlreadyExists() throws Exception {
        String jwt = Utils.getToken(mockMvc, "test_maintainer", "test");

        mockMvc.perform(post("/market")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt)
                        .content("{ \"mic\": \"MISX\", \"location\": \"Not Moscow\" }")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    public void createMarketUnknownMIC() throws Exception {
        String jwt = Utils.getToken(mockMvc, "test_maintainer", "test");

        mockMvc.perform(post("/market")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt)
                        .content("{ \"mic\": \"UNKW\", \"location\": \"Unknown\" }")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
