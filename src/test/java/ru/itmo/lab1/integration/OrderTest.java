package ru.itmo.lab1.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.itmo.lab1.model.dto.OrderDto;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class OrderTest {

    @Container
    private static final PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:13.1-alpine")
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper om;

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

    @ParameterizedTest
    @ValueSource(strings = {"REJECTED", "FULFILLED", "CANCELLED"})
    public void createOrderBadStatus(String status) throws Exception {
        String jwt = Utils.getToken(mockMvc, "test_trader", "test");

        mockMvc.perform(post("/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt)
                        .content("{ \"idMiidId\": \"30b0f4b5-a941-4878-8926-56e5426a5ec4\"," +
                                "\"status\": \"" + status + "\"," +
                                "\"orderType\": \"MARKET\"," +
                                "\"volume\": 10," +
                                "\"price\": 10," +
                                "\"side\": \"BUY\"," +
                                "\"created_datetime\": \"2022-06-31T23:59:59Z\" }"
                        )
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @ParameterizedTest
    @ValueSource(strings = {"test_maintainer", "test_trader"})
    public void createOrderForOtherUserFromTrader(String name) throws Exception {
        String jwt = Utils.getToken(mockMvc, name, "test");

        mockMvc.perform(post("/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt)
                        .content("{ \"idMiidId\": \"30b0f4b5-a941-4878-8926-56e5426a5ec4\"," +
                                "\"idUserId\": \"a3661347-ad2d-4628-8339-99e9c449340e\"," +
                                "\"status\": \"ACTIVE\"," +
                                "\"orderType\": \"MARKET\"," +
                                "\"volume\": 10," +
                                "\"price\": 10," +
                                "\"side\": \"BUY\"," +
                                "\"created_datetime\": \"2022-06-31T23:59:59Z\" }"
                        )
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void createOrderForOtherUserFromAdmin() throws Exception {
        String jwt = Utils.getToken(mockMvc, "test_admin", "test");

        MockHttpServletResponse response = mockMvc.perform(post("/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt)
                        .content("{ \"idMiidId\": \"30b0f4b5-a941-4878-8926-56e5426a5ec4\"," +
                                "\"idUserId\": \"a3661347-ad2d-4628-8339-99e9c449340e\"," +
                                "\"status\": \"ACTIVE\"," +
                                "\"orderType\": \"MARKET\"," +
                                "\"volume\": 10," +
                                "\"price\": 10," +
                                "\"side\": \"BUY\"," +
                                "\"created_datetime\": \"2022-06-31T23:59:59Z\" }"
                        )
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        OrderDto createdOrder = om.readValue(response.getContentAsString(), OrderDto.class);

        mockMvc.perform(get("/order/" + createdOrder.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt)
                        .content("{ \"status\": \"FULFILLED\" }")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void createOrderAndFulfilAndCheckTradeCreated() throws Exception {
        String jwt = Utils.getToken(mockMvc, "test_trader", "test");

        MockHttpServletResponse response = mockMvc.perform(post("/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt)
                        .content("{ \"idMiidId\": \"30b0f4b5-a941-4878-8926-56e5426a5ec4\"," +
                                "\"status\": \"ACTIVE\"," +
                                "\"orderType\": \"MARKET\"," +
                                "\"volume\": 10," +
                                "\"price\": 10," +
                                "\"side\": \"BUY\"," +
                                "\"created_datetime\": \"2022-06-31T23:59:59Z\" }"
                        )
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        OrderDto createdOrder = om.readValue(response.getContentAsString(), OrderDto.class);

        mockMvc.perform(patch("/order/" + createdOrder.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt)
                        .content("{ \"status\": \"FULFILLED\" }")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get("/trade")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[?(@.idOrderId == \"" + createdOrder.getId() + "\")]").exists());
    }

    @Test
    public void statusTransitions() throws Exception {
        String jwt = Utils.getToken(mockMvc, "test_trader", "test");

        MockHttpServletResponse response = mockMvc.perform(post("/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt)
                        .content("{ \"idMiidId\": \"30b0f4b5-a941-4878-8926-56e5426a5ec4\"," +
                                "\"status\": \"ACTIVE\"," +
                                "\"orderType\": \"MARKET\"," +
                                "\"volume\": 10," +
                                "\"price\": 10," +
                                "\"side\": \"BUY\"," +
                                "\"created_datetime\": \"2022-06-31T23:59:59Z\" }"
                        )
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        OrderDto createdOrder = om.readValue(response.getContentAsString(), OrderDto.class);

        mockMvc.perform(patch("/order/" + createdOrder.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt)
                        .content("{ \"status\": \"REJECTED\" }")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(patch("/order/" + createdOrder.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt)
                        .content("{ \"status\": \"FULFILLED\" }")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());

        mockMvc.perform(patch("/order/" + createdOrder.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt)
                        .content("{ \"status\": \"CANCELLED\" }")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }
}
