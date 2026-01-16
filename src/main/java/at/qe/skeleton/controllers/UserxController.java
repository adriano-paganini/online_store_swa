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
 * Userx endpoints exposed by the server.
 *
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


    @GetMapping("/me")
    public ResponseEntity<UserxMeDTO> getMe() {
        Userx user = authenticatedUserService.requireAuthenticatedUser();
        return ResponseEntity.ok(userxMeMapper.mapTo(user));
    }

    @PatchMapping("/me")
    public ResponseEntity<UserxMeDTO> updateMe(
            @Valid @RequestBody UserxMeUpdateDTO dto
    ) {
        Userx updated = userxService.updateCurrentUser(dto);
        return ResponseEntity.ok(userxMeMapper.mapTo(updated));
    }
     
    @GetMapping("/authenticated")
    public ResponseEntity<String> isAuthenticated(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body("User not authenticated");
        }
        return ResponseEntity.ok("User is authenticated: " + userDetails.getUsername());
    }
}
