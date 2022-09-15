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
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class AuthTest {

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

    private static Stream<Arguments> provideGoodCredentials() {
        return Stream.of(
                Arguments.of("test_admin", "test"),
                Arguments.of("test_maintainer", "test"),
                Arguments.of("test_trader", "test")
        );
    }

    private static Stream<Arguments> provideBadCredentials() {
        return Stream.of(
                Arguments.of("wtf", "wtf"),
                Arguments.of("", ""),
                Arguments.of("test_trader", ""),
                Arguments.of("test_trader", "BadPass"),
                Arguments.of(null, "pass"),
                Arguments.of("test_trader", null),
                Arguments.of(null, null)
        );
    }

    @ParameterizedTest
    @MethodSource("provideGoodCredentials")
    public void loginExistingUser(String name, String pass) throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"name\": \"" + name + "\", \"password\": \"" + pass + "\" }")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().exists("Authorization"));
    }

    @ParameterizedTest
    @MethodSource("provideBadCredentials")
    public void loginBadCredentials(String name, String pass) throws Exception {
        String contentBuilder = "{ \"name\":" + (name == null ? "null" : '"' + name + '"') +
                ", \"password\":" +
                (pass == null ? "null" : '"' + pass + '"') +
                "}";

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(contentBuilder)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(header().doesNotExist("Authorization"));
    }
}
