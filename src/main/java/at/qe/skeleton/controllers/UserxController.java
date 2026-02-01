package at.qe.skeleton.controllers;

import at.qe.skeleton.dtos.UserxMeDTO;
import at.qe.skeleton.dtos.UserxMeUpdateDTO;
import at.qe.skeleton.mappers.UserxMeMapper;
import at.qe.skeleton.model.Userx;
import at.qe.skeleton.services.AuthenticatedUserService;
import at.qe.skeleton.services.UserxService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for accessing and managing the authenticated user's profile.
 *
 * <p>
 * Provides endpoints to retrieve and update the currently authenticated user's
 * personal data.
 * </p>
 * This class is part of the skeleton project provided for students of the
 * course "Software Architecture" offered by Innsbruck University.
 */
@RestController
@RequestMapping("/users")
public class UserxController {
 
    private final UserxMeMapper userxMeMapper;
    private final UserxService userxService;
    private final AuthenticatedUserService authenticatedUserService;

    @Autowired
    public UserxController(UserxMeMapper userMeMapper, UserxService userService, AuthenticatedUserService authenticatedUserService) {
        this.userxMeMapper = userMeMapper;
        this.userxService = userService;
        this.authenticatedUserService = authenticatedUserService;
    }

    /**
     * Retrieves the profile of the authenticated user.
     *
     * <p>Possible responses:</p>
     * <ul>
     *   <li>200 OK - user profile successfully retrieved</li>
     * </ul>
     *
     * @return the authenticated user's profile
     */
    @GetMapping("/me")
    public ResponseEntity<UserxMeDTO> getMe() {
        Userx user = authenticatedUserService.requireAuthenticatedUser();
        return ResponseEntity.ok(userxMeMapper.mapTo(user));
    }

    /**
     * Updates the profile of the authenticated user.
     *
     * <p>Possible responses:</p>
     * <ul>
     *   <li>200 OK - user profile successfully updated</li>
     *   <li>400 Bad Request - invalid update data</li>
     * </ul>
     *
     * @param dto updated user data
     * @return the updated user profile
     */
    @PatchMapping("/me")
    public ResponseEntity<UserxMeDTO> updateMe(
            @Valid @RequestBody UserxMeUpdateDTO dto
    ) {
        Userx updated = userxService.updateCurrentUser(dto);
        return ResponseEntity.ok(userxMeMapper.mapTo(updated));
    }

    /**
     * Checks whether the current request is authenticated.
     *
     * <p>This endpoint is intended for diagnostic purposes.</p>
     *
     * <p>Possible responses:</p>
     * <ul>
     *   <li>200 OK - user is authenticated</li>
     *   <li>401 Unauthorized - user is not authenticated</li>
     * </ul>
     *
     * @return authentication status message
     */
    @GetMapping("/authenticated")
    public ResponseEntity<String> isAuthenticated(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body("User not authenticated");
        }
        return ResponseEntity.ok("User is authenticated: " + userDetails.getUsername());
    }
}
