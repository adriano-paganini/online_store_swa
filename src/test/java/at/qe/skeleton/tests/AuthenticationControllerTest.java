package at.qe.skeleton.tests;

import at.qe.skeleton.controllers.AuthenticationController;
import at.qe.skeleton.dtos.LoginRequestDTO;
import at.qe.skeleton.services.AuthenticationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(AuthenticationController.class)
@AutoConfigureMockMvc
public class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthenticationService authenticationService;

    private static final String VALID_USERNAME = "testuser";
    private static final String VALID_PASSWORD = "password123";
    private static final String INVALID_USERNAME = "invaliduser";
    private static final String INVALID_PASSWORD = "wrongpassword";
    private static final String MOCK_TOKEN = "mock.jwt.token.here";

    private Authentication mockAuthentication;

    @BeforeEach
    void setUp() {
        mockAuthentication = Mockito.mock(Authentication.class);
        Mockito.when(mockAuthentication.getName()).thenReturn(VALID_USERNAME);
    }

    @Test
    void authenticateUserSuccess() throws Exception {
        LoginRequestDTO loginRequest = new LoginRequestDTO(VALID_USERNAME, VALID_PASSWORD);

        Mockito.when(authenticationService.authenticateLoginRequest(VALID_USERNAME, VALID_PASSWORD))
                .thenReturn(mockAuthentication);
        Mockito.when(authenticationService.generateToken(mockAuthentication))
                .thenReturn(MOCK_TOKEN);

        mockMvc.perform(MockMvcRequestBuilders.post("/authentication/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.bearerToken").value(MOCK_TOKEN));

        Mockito.verify(authenticationService).authenticateLoginRequest(VALID_USERNAME, VALID_PASSWORD);
        Mockito.verify(authenticationService).generateToken(mockAuthentication);
    }

    @Test
    void authenticateUserWithInvalidCredentials() throws Exception {
        LoginRequestDTO loginRequest = new LoginRequestDTO(INVALID_USERNAME, INVALID_PASSWORD);

        Mockito.when(authenticationService.authenticateLoginRequest(INVALID_USERNAME, INVALID_PASSWORD))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        mockMvc.perform(MockMvcRequestBuilders.post("/authentication/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());

        Mockito.verify(authenticationService).authenticateLoginRequest(INVALID_USERNAME, INVALID_PASSWORD);
        Mockito.verify(authenticationService, Mockito.never()).generateToken(Mockito.any());
    }

    @Test
    void authenticateUserWithEmptyUsername() throws Exception {
        LoginRequestDTO loginRequest = new LoginRequestDTO("", VALID_PASSWORD);

        mockMvc.perform(MockMvcRequestBuilders.post("/authentication/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        Mockito.verify(authenticationService, Mockito.never()).authenticateLoginRequest(Mockito.anyString(), Mockito.anyString());
    }

    @Test
    void authenticateUserWithEmptyPassword() throws Exception {
        LoginRequestDTO loginRequest = new LoginRequestDTO(VALID_USERNAME, "");

        mockMvc.perform(MockMvcRequestBuilders.post("/authentication/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        Mockito.verify(authenticationService, Mockito.never()).authenticateLoginRequest(Mockito.anyString(), Mockito.anyString());
    }

    @Test
    void authenticateUserWithNullUsername() throws Exception {
        String json = "{\"password\":\"" + VALID_PASSWORD + "\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/authentication/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        Mockito.verify(authenticationService, Mockito.never()).authenticateLoginRequest(Mockito.anyString(), Mockito.anyString());
    }

    @Test
    void authenticateUserWithNullPassword() throws Exception {
        String json = "{\"username\":\"" + VALID_USERNAME + "\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/authentication/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        Mockito.verify(authenticationService, Mockito.never()).authenticateLoginRequest(Mockito.anyString(), Mockito.anyString());
    }

    @Test
    void authenticateUserWithInvalidJson() throws Exception {
        String invalidJson = "{invalid json}";

        mockMvc.perform(MockMvcRequestBuilders.post("/authentication/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        Mockito.verify(authenticationService, Mockito.never()).authenticateLoginRequest(Mockito.anyString(), Mockito.anyString());
    }

    @Test
    void authenticateUserWithMissingContentType() throws Exception {
        LoginRequestDTO loginRequest = new LoginRequestDTO(VALID_USERNAME, VALID_PASSWORD);

        mockMvc.perform(MockMvcRequestBuilders.post("/authentication/login")
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(MockMvcResultMatchers.status().isUnsupportedMediaType());

        Mockito.verify(authenticationService, Mockito.never()).authenticateLoginRequest(Mockito.anyString(), Mockito.anyString());
    }

    @Test
    void authenticateUserWithAuthenticationCredentialsNotFoundException() throws Exception {
        LoginRequestDTO loginRequest = new LoginRequestDTO(VALID_USERNAME, VALID_PASSWORD);

        Mockito.when(authenticationService.authenticateLoginRequest(VALID_USERNAME, VALID_PASSWORD))
                .thenThrow(new AuthenticationCredentialsNotFoundException("Authentication credentials not found"));

        mockMvc.perform(MockMvcRequestBuilders.post("/authentication/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());

        Mockito.verify(authenticationService).authenticateLoginRequest(VALID_USERNAME, VALID_PASSWORD);
        Mockito.verify(authenticationService, Mockito.never()).generateToken(Mockito.any());
    }
}
