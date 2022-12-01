package ru.itmo.lab2.market_service.integration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    }

    @Test
    public void createMarket() throws Exception {
//        String jwt = Utils.getToken(mockMvc, "test_maintainer", "test");

        mockMvc.perform(post("/market")
                        .contentType(MediaType.APPLICATION_JSON)
//                        .header("Authorization", "Bearer " + jwt)
                        .content("{ \"mic\": \"XNAS\", \"location\": \"New York\" }")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.mic").value("XNAS"))
                .andExpect(jsonPath("$.location").value("New York"));
    }

//    @Test
//    public void createMarketMICAlreadyExists() throws Exception {
////        String jwt = Utils.getToken(mockMvc, "test_maintainer", "test");
//
//        mockMvc.perform(post("/market")
//                        .contentType(MediaType.APPLICATION_JSON)
////                        .header("Authorization", "Bearer " + jwt)
//                        .content("{ \"mic\": \"MISX\", \"location\": \"Not Moscow\" }")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isConflict());
//    }

    @Test
    public void createMarketUnknownMIC() throws Exception {
//        String jwt = Utils.getToken(mockMvc, "test_maintainer", "test");

        mockMvc.perform(post("/market")
                        .contentType(MediaType.APPLICATION_JSON)
//                        .header("Authorization", "Bearer " + jwt)
                        .content("{ \"mic\": \"UNKW\", \"location\": \"Unknown\" }")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getNoParams() throws Exception {
        mockMvc.perform(get("/market")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(header().doesNotExist("x-total-count"))
                .andExpect(jsonPath("$.hasMore").value(false))
                .andExpect(jsonPath("$.items").isArray());
    }

    @Test
    public void getPageWithSize() throws Exception {
        mockMvc.perform(get("/market")
                        .param("size", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.hasMore").doesNotExist())
                .andExpect(header().exists("x-total-count"))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    public void getPageWithNoContent() throws Exception {
        mockMvc.perform(get("/market")
                        .param("page", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
