package at.qe.skeleton.controllers;

import at.qe.skeleton.dtos.UserxMeDTO;
import at.qe.skeleton.dtos.UserxUpdateDTO;
import at.qe.skeleton.mappers.UserxMapper;
import at.qe.skeleton.model.Userx;
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
@RequestMapping("/api/users")
public class UserxController {
 
    private final UserxMapper userxMapper;
    private final UserxService userxService;

    @Autowired
    public UserxController(UserxMapper userMapper, UserxService userService) {
        this.userxMapper = userMapper;
        this.userxService = userService;
    }


    @GetMapping("/me")
    public ResponseEntity<UserxMeDTO> getMe() {
        Userx user = userxService.getCurrentUser();
        return ResponseEntity.ok(userxMapper.mapToMe(user));
    }

    @PatchMapping("/me")
    public ResponseEntity<UserxMeDTO> updateMe(
            @Valid @RequestBody UserxUpdateDTO dto
    ) {
        Userx updated = userxService.updateCurrentUser(dto);
        return ResponseEntity.ok(userxMapper.mapToMe(updated));
    }
     
    @GetMapping("/authenticated")
    public ResponseEntity<String> isAuthenticated(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body("User not authenticated");
        }
        return ResponseEntity.ok("User is authenticated: " + userDetails.getUsername());
    }
}
