package at.qe.skeleton.tests;

import at.qe.skeleton.dtos.UserxUpdateDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserxControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "user2", authorities = {"CUSTOMER"})
    public void testGetMe() throws Exception {

        mockMvc.perform(get("/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("user2"))
                .andExpect(jsonPath("$.roles").value("CUSTOMER"))
                .andExpect(jsonPath("$.id").doesNotExist());
    }

    @Test
    @WithMockUser(username = "user2", authorities = {"CUSTOMER"})
    public void testUpdateMe() throws Exception {

        UserxUpdateDTO dto = new UserxUpdateDTO(
                "unchanged",
                null,
                "HttpUpdated",
                "unchanged",
                "http@update.at",
                null,
                null
        );

        mockMvc.perform(patch("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("HttpUpdated"))
                .andExpect(jsonPath("$.email").value("http@update.at"));
    }

    @Test
    public void testGetMeUnauthenticated() throws Exception {

        mockMvc.perform(get("/users/me"))
                .andExpect(status().isUnauthorized());
    }
}
