package at.qe.skeleton.tests;

import at.qe.skeleton.model.Userx;
import at.qe.skeleton.repositories.UserxRepository;
import at.qe.skeleton.services.AuthenticatedUserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticatedUserServiceTest {

    @Mock
    private UserxRepository userRepository;

    @InjectMocks
    private AuthenticatedUserService authenticatedUserService;

    private Userx testUser;
    private Authentication mockAuthentication;
    private SecurityContext mockSecurityContext;

    @BeforeEach
    void setUp() {
        testUser = new Userx();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setEmail("test@example.com");

        mockAuthentication = mock(Authentication.class);
        mockSecurityContext = mock(SecurityContext.class);
    }

    @AfterEach
    void tearDown() {
        // Clear SecurityContext after each test
        SecurityContextHolder.clearContext();
    }

    @Test
    void getAuthenticatedUserReturnsUserWhenAuthenticated() {
        // Setup: authenticated user
        when(mockAuthentication.isAuthenticated()).thenReturn(true);
        when(mockAuthentication.getName()).thenReturn("testuser");
        when(mockAuthentication.getPrincipal()).thenReturn("testuser");
        when(mockSecurityContext.getAuthentication()).thenReturn(mockAuthentication);
        SecurityContextHolder.setContext(mockSecurityContext);

        when(userRepository.findFirstByUsername("testuser")).thenReturn(Optional.of(testUser));

        Userx result = authenticatedUserService.getAuthenticatedUser();

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals(1L, result.getId());
        verify(userRepository, times(1)).findFirstByUsername("testuser");
    }

    @Test
    void getAuthenticatedUserReturnsNullWhenUserNotFound() {
        // Setup: authenticated but user not in repository
        when(mockAuthentication.isAuthenticated()).thenReturn(true);
        when(mockAuthentication.getName()).thenReturn("nonexistent");
        when(mockAuthentication.getPrincipal()).thenReturn("nonexistent");
        when(mockSecurityContext.getAuthentication()).thenReturn(mockAuthentication);
        SecurityContextHolder.setContext(mockSecurityContext);

        when(userRepository.findFirstByUsername("nonexistent")).thenReturn(Optional.empty());

        Userx result = authenticatedUserService.getAuthenticatedUser();

        assertNull(result);
        verify(userRepository, times(1)).findFirstByUsername("nonexistent");
    }

    @Test
    void getAuthenticatedUserReturnsNullWhenAuthenticationIsNull() {
        // Setup: null authentication
        when(mockSecurityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(mockSecurityContext);

        Userx result = authenticatedUserService.getAuthenticatedUser();

        assertNull(result);
        verify(userRepository, never()).findFirstByUsername(anyString());
    }

    @Test
    void getAuthenticatedUserReturnsNullWhenNotAuthenticated() {
        // Setup: authentication exists but not authenticated
        when(mockAuthentication.isAuthenticated()).thenReturn(false);
        when(mockSecurityContext.getAuthentication()).thenReturn(mockAuthentication);
        SecurityContextHolder.setContext(mockSecurityContext);

        Userx result = authenticatedUserService.getAuthenticatedUser();

        assertNull(result);
        verify(userRepository, never()).findFirstByUsername(anyString());
    }

    @Test
    void getAuthenticatedUserReturnsNullWhenAnonymousUser() {
        // Setup: anonymous user
        when(mockAuthentication.isAuthenticated()).thenReturn(true);
        when(mockAuthentication.getPrincipal()).thenReturn("anonymousUser");
        when(mockSecurityContext.getAuthentication()).thenReturn(mockAuthentication);
        SecurityContextHolder.setContext(mockSecurityContext);

        Userx result = authenticatedUserService.getAuthenticatedUser();

        assertNull(result);
        verify(userRepository, never()).findFirstByUsername(anyString());
    }

    @Test
    void getAuthenticatedUserReturnsNullWhenSecurityContextIsNull() {
        // Setup: SecurityContext is null (edge case)
        SecurityContextHolder.clearContext();

        Userx result = authenticatedUserService.getAuthenticatedUser();

        assertNull(result);
        verify(userRepository, never()).findFirstByUsername(anyString());
    }

    @Test
    void requireAuthenticatedUserReturnsUserWhenAuthenticated() {
        // Setup: authenticated user
        when(mockAuthentication.isAuthenticated()).thenReturn(true);
        when(mockAuthentication.getName()).thenReturn("testuser");
        when(mockAuthentication.getPrincipal()).thenReturn("testuser");
        when(mockSecurityContext.getAuthentication()).thenReturn(mockAuthentication);
        SecurityContextHolder.setContext(mockSecurityContext);

        when(userRepository.findFirstByUsername("testuser")).thenReturn(Optional.of(testUser));

        Userx result = authenticatedUserService.requireAuthenticatedUser();

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals(1L, result.getId());
        verify(userRepository, times(1)).findFirstByUsername("testuser");
    }

    @Test
    void requireAuthenticatedUserThrowsExceptionWhenUserNotFound() {
        // Setup: authenticated but user not in repository
        when(mockAuthentication.isAuthenticated()).thenReturn(true);
        when(mockAuthentication.getName()).thenReturn("nonexistent");
        when(mockAuthentication.getPrincipal()).thenReturn("nonexistent");
        when(mockSecurityContext.getAuthentication()).thenReturn(mockAuthentication);
        SecurityContextHolder.setContext(mockSecurityContext);

        when(userRepository.findFirstByUsername("nonexistent")).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            authenticatedUserService.requireAuthenticatedUser();
        });

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        assertEquals("User not authenticated", exception.getReason());
        verify(userRepository, times(1)).findFirstByUsername("nonexistent");
    }

    @Test
    void requireAuthenticatedUserThrowsExceptionWhenAuthenticationIsNull() {
        // Setup: null authentication
        when(mockSecurityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(mockSecurityContext);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            authenticatedUserService.requireAuthenticatedUser();
        });

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        assertEquals("User not authenticated", exception.getReason());
        verify(userRepository, never()).findFirstByUsername(anyString());
    }

    @Test
    void requireAuthenticatedUserThrowsExceptionWhenNotAuthenticated() {
        // Setup: authentication exists but not authenticated
        when(mockAuthentication.isAuthenticated()).thenReturn(false);
        when(mockSecurityContext.getAuthentication()).thenReturn(mockAuthentication);
        SecurityContextHolder.setContext(mockSecurityContext);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            authenticatedUserService.requireAuthenticatedUser();
        });

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        assertEquals("User not authenticated", exception.getReason());
        verify(userRepository, never()).findFirstByUsername(anyString());
    }

    @Test
    void requireAuthenticatedUserThrowsExceptionWhenAnonymousUser() {
        // Setup: anonymous user
        when(mockAuthentication.isAuthenticated()).thenReturn(true);
        when(mockAuthentication.getPrincipal()).thenReturn("anonymousUser");
        when(mockSecurityContext.getAuthentication()).thenReturn(mockAuthentication);
        SecurityContextHolder.setContext(mockSecurityContext);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            authenticatedUserService.requireAuthenticatedUser();
        });

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        assertEquals("User not authenticated", exception.getReason());
        verify(userRepository, never()).findFirstByUsername(anyString());
    }

    @Test
    void requireAuthenticatedUserThrowsExceptionWhenSecurityContextIsNull() {
        // Setup: SecurityContext is null
        SecurityContextHolder.clearContext();

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            authenticatedUserService.requireAuthenticatedUser();
        });

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        assertEquals("User not authenticated", exception.getReason());
        verify(userRepository, never()).findFirstByUsername(anyString());
    }

    @Test
    void getAuthenticatedUserHandlesDifferentPrincipalTypes() {
        // Setup: principal is not a string (edge case)
        when(mockAuthentication.isAuthenticated()).thenReturn(true);
        when(mockAuthentication.getName()).thenReturn("testuser");
        when(mockAuthentication.getPrincipal()).thenReturn(12345L); // Non-string principal
        when(mockSecurityContext.getAuthentication()).thenReturn(mockAuthentication);
        SecurityContextHolder.setContext(mockSecurityContext);

        when(userRepository.findFirstByUsername("testuser")).thenReturn(Optional.of(testUser));

        Userx result = authenticatedUserService.getAuthenticatedUser();

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository, times(1)).findFirstByUsername("testuser");
    }

    @Test
    void requireAuthenticatedUserCallsGetAuthenticatedUser() {
        // Setup: authenticated user
        when(mockAuthentication.isAuthenticated()).thenReturn(true);
        when(mockAuthentication.getName()).thenReturn("testuser");
        when(mockAuthentication.getPrincipal()).thenReturn("testuser");
        when(mockSecurityContext.getAuthentication()).thenReturn(mockAuthentication);
        SecurityContextHolder.setContext(mockSecurityContext);

        when(userRepository.findFirstByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Call requireAuthenticatedUser which internally calls getAuthenticatedUser
        Userx result = authenticatedUserService.requireAuthenticatedUser();

        assertNotNull(result);
        // Verify that repository was called (indirectly through getAuthenticatedUser)
        verify(userRepository, times(1)).findFirstByUsername("testuser");
    }
}
