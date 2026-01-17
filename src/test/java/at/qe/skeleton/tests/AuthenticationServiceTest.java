package at.qe.skeleton.tests;

import at.qe.skeleton.configs.JwtTokenProvider;
import at.qe.skeleton.services.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider tokenProvider;

    @InjectMocks
    private AuthenticationService authenticationService;

    private static final String VALID_USERNAME = "testuser";
    private static final String VALID_PASSWORD = "password123";
    private static final String INVALID_USERNAME = "invaliduser";
    private static final String INVALID_PASSWORD = "wrongpassword";
    private static final String MOCK_TOKEN = "mock.jwt.token.here";

    private Authentication mockAuthentication;

    @BeforeEach
    void setUp() {
        mockAuthentication = mock(Authentication.class);
        when(mockAuthentication.getName()).thenReturn(VALID_USERNAME);
    }

    @Test
    void authenticateLoginRequestSuccess() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuthentication);

        Authentication result = authenticationService.authenticateLoginRequest(VALID_USERNAME, VALID_PASSWORD);

        assertNotNull(result);
        assertEquals(mockAuthentication, result);
        verify(authenticationManager).authenticate(argThat(token ->
                token instanceof UsernamePasswordAuthenticationToken &&
                        VALID_USERNAME.equals(token.getPrincipal()) &&
                        VALID_PASSWORD.equals(token.getCredentials())
        ));
    }

    @Test
    void authenticateLoginRequestWithInvalidCredentials() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        assertThrows(BadCredentialsException.class, () -> {
            authenticationService.authenticateLoginRequest(INVALID_USERNAME, INVALID_PASSWORD);
        });

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void authenticateLoginRequestWithNullUsername() {
        assertThrows(Exception.class, () -> {
            authenticationService.authenticateLoginRequest(null, VALID_PASSWORD);
        });
    }

    @Test
    void authenticateLoginRequestWithNullPassword() {
        assertThrows(Exception.class, () -> {
            authenticationService.authenticateLoginRequest(VALID_USERNAME, null);
        });
    }

    @Test
    void authenticateLoginRequestWithEmptyUsername() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuthentication);

        Authentication result = authenticationService.authenticateLoginRequest("", VALID_PASSWORD);

        assertNotNull(result);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void authenticateLoginRequestWithEmptyPassword() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuthentication);

        Authentication result = authenticationService.authenticateLoginRequest(VALID_USERNAME, "");

        assertNotNull(result);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void generateTokenSuccess() {
        when(tokenProvider.generate(mockAuthentication)).thenReturn(MOCK_TOKEN);

        String result = authenticationService.generateToken(mockAuthentication);

        assertNotNull(result);
        assertEquals(MOCK_TOKEN, result);
        verify(tokenProvider).generate(mockAuthentication);
    }

    @Test
    void generateTokenWithNullAuthentication() {
        assertThrows(Exception.class, () -> {
            authenticationService.generateToken(null);
        });
    }

    @Test
    void authenticateAndGenerateTokenFlow() {
        // Test the complete flow: authenticate then generate token
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuthentication);
        when(tokenProvider.generate(mockAuthentication)).thenReturn(MOCK_TOKEN);

        Authentication auth = authenticationService.authenticateLoginRequest(VALID_USERNAME, VALID_PASSWORD);
        String token = authenticationService.generateToken(auth);

        assertNotNull(auth);
        assertNotNull(token);
        assertEquals(MOCK_TOKEN, token);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenProvider).generate(mockAuthentication);
    }

    @Test
    void authenticateLoginRequestCreatesCorrectToken() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuthentication);

        authenticationService.authenticateLoginRequest(VALID_USERNAME, VALID_PASSWORD);

        verify(authenticationManager).authenticate(argThat(token -> {
            if (token instanceof UsernamePasswordAuthenticationToken) {
                UsernamePasswordAuthenticationToken upat = (UsernamePasswordAuthenticationToken) token;
                return VALID_USERNAME.equals(upat.getPrincipal()) &&
                       VALID_PASSWORD.equals(upat.getCredentials());
            }
            return false;
        }));
    }
}
