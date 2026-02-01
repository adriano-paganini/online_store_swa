/**
 * Spring configuration for web security.
 * <p>
 * This class is part of the skeleton project provided for students of the
 * course "Software Architecture" offered by Innsbruck University.
 */

package at.qe.skeleton.services;

import at.qe.skeleton.configs.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/**
 * Service for managing authentication.
 * <p>
 * This service handles the core business logic for authenticating users and creating JWT tokens.
 */
@Service
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    @Autowired
    public AuthenticationService(AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    /**
     * Authenticates a user with the given username and password.
     * @param username the username
     * @param password the password
     * @return the authentication object
     * @throws IllegalArgumentException if username or password are {@code null}
     */
    public Authentication authenticateLoginRequest(String username, String password) {
        if (username == null || password == null) {
            throw new IllegalArgumentException("Username and password cannot be null");
        }
        return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }

    /**
     * Generates a JWT token for the given authentication object.
     * @param authentication the authentication object
     * @return the generated JWT token
     * @throws IllegalArgumentException if authentication is null
     */
    public String generateToken(Authentication authentication) {
        if (authentication == null) {
            throw new IllegalArgumentException("Authentication cannot be null");
        }
        return tokenProvider.generate(authentication);
    }

}
