package ru.itmo.lab1.integration;

import org.junit.jupiter.params.provider.Arguments;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class Utils {
    static String getToken(MockMvc mockMvc, String name, String password) throws Exception {
        MockHttpServletResponse response = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"name\": \"" + name + "\", \"password\": \"" + password + "\" }")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().exists("Authorization"))
                .andReturn().getResponse();
        return response.getHeader("Authorization");
    }

    static Stream<Arguments> provideEndpoints() {
        return Stream.of(
                Arguments.of("market"),
                Arguments.of("instrument"),
                Arguments.of("marketInstrumentId"),
                Arguments.of("users"),
                Arguments.of("order"),
                Arguments.of("trade"),
                Arguments.of("portfolio")
        );
    }
}
