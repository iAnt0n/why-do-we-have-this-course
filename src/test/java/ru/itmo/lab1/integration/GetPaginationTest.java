package ru.itmo.lab1.integration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
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

import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class GetPaginationTest {

    @Container
    private static final PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:13.1-alpine")
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private MockMvc mockMvc;

    private String headerAuth = "Bearer ";

    @BeforeAll
    public static void setUp() {
        container.start();
    }

    @AfterAll
    public static void tearDown() {
        container.stop();
    }

    @BeforeEach
    public void setUpEach() throws Exception {
        headerAuth += Utils.getToken(mockMvc, "test_admin", "test");
    }

    @DynamicPropertySource
    public static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", container::getJdbcUrl);
        registry.add("spring.datasource.username", container::getUsername);
        registry.add("spring.datasource.password", container::getPassword);
        registry.add("spring.liquibase.changelog", () -> "classpath:db/changelog/db.changelog-master-test.xml");
    }

    @ParameterizedTest
    @MethodSource("ru.itmo.lab1.integration.Utils#provideEndpoints")
    public void getNoParams(String endpoint) throws Exception {
        mockMvc.perform(get("/" + endpoint)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", headerAuth))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(header().doesNotExist("x-total-count"))
                .andExpect(jsonPath("$.hasMore").value(false))
                .andExpect(jsonPath("$.items").isArray());
    }

    @ParameterizedTest
    @MethodSource("ru.itmo.lab1.integration.Utils#provideEndpoints")
    public void getPageWithSize(String endpoint) throws Exception {
        mockMvc.perform(get("/" + endpoint)
                        .param("size", "1")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", headerAuth))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.hasMore").doesNotExist())
                .andExpect(header().exists("x-total-count"))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @ParameterizedTest
    @MethodSource("ru.itmo.lab1.integration.Utils#provideEndpoints")
    public void getPageWithNoContent(String endpoint) throws Exception {
        mockMvc.perform(get("/" + endpoint)
                        .param("page", "10")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", headerAuth))
                .andExpect(status().isNoContent());
    }
}
