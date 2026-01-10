package at.qe.skeleton.services;

import at.qe.skeleton.model.Userx;
import at.qe.skeleton.repositories.UserxRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Service for accessing currently authenticated user.
 *
 * This class is part of the skeleton project provided for students of the
 * course "Software Architecture" offered by Innsbruck University.
 */
@Service
public class AuthenticatedUserService {

    private final UserxRepository userRepository;

    @Autowired
    public AuthenticatedUserService(UserxRepository userxRepository) {
        this.userRepository = userxRepository;
    }

    /**
     * Returns the currently authenticated user.
     *
     * @return the authenticated user or null
     */
    public Userx getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }
        return userRepository.findFirstByUsername(auth.getName()).orElse(null);
    }

    /**
     * helper method to verify user where attributes are requested is not null
     *
     * @return authenticated user
     */
    public Userx requireAuthenticatedUser() {
        Userx user = getAuthenticatedUser();
        if (user == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "User not authenticated");
        }
        return user;
    }

}
