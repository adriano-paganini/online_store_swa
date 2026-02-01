package at.qe.skeleton.controllers;

import at.qe.skeleton.dtos.PageResponseDTO;
import at.qe.skeleton.dtos.UserxAdminCreateDTO;
import at.qe.skeleton.dtos.UserxDTO;
import at.qe.skeleton.dtos.UserxUpdateDTO;
import at.qe.skeleton.mappers.UserxAdminCreateMapper;
import at.qe.skeleton.mappers.UserxMapper;
import at.qe.skeleton.model.Userx;
import at.qe.skeleton.model.UserxRole;
import at.qe.skeleton.services.UserxService;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/**
 * REST controller for administrative user management.
 *
 * <p>
 * All endpoints are restricted to users with administrative privileges
 * and allow managing user accounts, including listing, creating,
 * updating, and deleting users.
 * </p>
 *
 * This class is part of the skeleton project provided for students of the
 * course "Software Architecture" offered by Innsbruck University.
 */
@RestController
@RequestMapping("/admin/users")
public class AdminController {
    private final UserxAdminCreateMapper userCreateMapper;
    private final UserxMapper userMapper;
    private final UserxService userService;

    @Autowired
    public AdminController(UserxAdminCreateMapper userCreateMapper, UserxMapper userMapper, UserxService userService) {
        this.userCreateMapper = userCreateMapper;
        this.userMapper = userMapper;
        this.userService = userService;
    }

    /**
     * Retrieves a paginated list of users.
     *
     * <p>Supports optional filtering by role and deletion status, as well
     * as pagination and sorting.</p>
     *
     * <p>Possible responses:</p>
     * <ul>
     *   <li>200 OK - users successfully retrieved</li>
     *   <li>400 Bad Request - invalid query parameters</li>
     * </ul>
     *
     * @return paginated list of users
     */
    @GetMapping("")
    public ResponseEntity<PageResponseDTO<UserxDTO>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) List<UserxRole> role,
            @RequestParam(required = false) Boolean deleted,
            @RequestParam(required = false, defaultValue = "id,desc") String sort
    ) {

        try{
            Page<Userx> userxPage = userService.getUsers(page,limit,role,deleted,sort);
            List<UserxDTO> userDTOs = userxPage.getContent().stream()
                    .map(userMapper::mapTo)
                    .toList();

            PageResponseDTO<UserxDTO> response = new PageResponseDTO<>(
                    userDTOs,
                    page,
                    limit,
                    userxPage.getTotalElements(),
                    userxPage.getTotalPages()
            );
            return ResponseEntity.ok(response);
        }catch(Exception e){
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error fetching Users: " + e.getMessage());
        }
    }



    /**
     * Retrieves a single user by its identifier.
     *
     * <p>Possible responses:</p>
     * <ul>
     *   <li>200 OK - user successfully retrieved</li>
     *   <li>404 Not Found - user does not exist</li>
     * </ul>
     *
     * @param id identifier of the user
     * @return the requested user
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserxDTO> getUser(@PathVariable Long id) {
        Optional<Userx> existingUserx = userService.loadUser(id);
        if (existingUserx.isPresent()) {
            return ResponseEntity.ok(userMapper.mapTo(existingUserx.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Creates a new user.
     *
     * <p>Possible responses:</p>
     * <ul>
     *   <li>201 Created - user successfully created</li>
     *   <li>400 Bad Request - validation failed</li>
     *   <li>409 Conflict - username already exists</li>
     * </ul>
     *
     * @param userxDto user data to create
     * @return the newly created user
     */
    @PostMapping("")
    public ResponseEntity<UserxDTO> createUser(@Valid @RequestBody UserxAdminCreateDTO userxDto) {
        Userx user = userService.saveUser(userCreateMapper.mapFrom(userxDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.mapTo(user));
    }

    /**
     * Partially updates an existing user.
     *
     * <p>Only a subset of user fields can be modified after creation.</p>
     *
     * <p>Possible responses:</p>
     * <ul>
     *   <li>200 OK - user successfully updated</li>
     *   <li>400 Bad Request - invalid update data</li>
     *   <li>404 Not Found - user does not exist</li>
     * </ul>
     *
     * @param id identifier of the user to update
     * @param dto updated user data
     * @return the updated user
     */
    @PatchMapping("/{id}")
    public ResponseEntity<UserxDTO> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserxUpdateDTO dto) {

        Userx updatedUser = userService.updateUser(id, dto);
        return ResponseEntity.ok(userMapper.mapTo(updatedUser));
    }

    /**
     * Deletes a user.
     *
     * <p>Possible responses:</p>
     * <ul>
     *   <li>204 No Content - user successfully deleted</li>
     *   <li>404 Not Found - user does not exist</li>
     * </ul>
     *
     * @param id identifier of the user to delete
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        Optional<Userx> existingUserx = userService.loadUser(id);
        if (existingUserx.isPresent()) {
            userService.deleteUser(existingUserx.get());
            return ResponseEntity.noContent().build();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
    }
}
