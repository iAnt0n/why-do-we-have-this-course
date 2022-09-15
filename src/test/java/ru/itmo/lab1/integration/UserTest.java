package ru.itmo.lab1.integration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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

import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class UserTest {

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

    private static Stream<Arguments> provideForbiddenCredentials() {
        return Stream.of(
                Arguments.of("test_maintainer", "test"),
                Arguments.of("test_trader", "test")
        );
    }

    private static Stream<Arguments> provideAdminCredentials() {
        return Stream.of(
                Arguments.of("test_admin", "test")
        );
    }

    @ParameterizedTest
    @MethodSource("provideForbiddenCredentials")
    public void withForbidden(String name, String pass) throws Exception {
        String jwt = Utils.getToken(mockMvc, name, pass);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt)
                        .content("{ \"name\": \"test\", \"password\": \"pass\", \"role\": \"ROLE_TRADER\" }")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authentication", "Bearer " + jwt)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }


    @ParameterizedTest
    @MethodSource("provideAdminCredentials")
    public void withAdmin(String name, String pass) throws Exception {
        String jwt = Utils.getToken(mockMvc, name, pass);

        MockHttpServletResponse response = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt)
                        .content("{ \"name\": \"test\", \"password\": \"pass\", \"role\": \"ROLE_TRADER\" }")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // created?
                .andReturn().getResponse();

        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[?(@.name == \"test\")]").exists());
    }
}
