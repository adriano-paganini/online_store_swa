package at.qe.skeleton.controllers;

import at.qe.skeleton.dtos.UserxMeDTO;
import at.qe.skeleton.dtos.UserxRegistrationDTO;
import at.qe.skeleton.mappers.UserxMeMapper;
import at.qe.skeleton.mappers.UserxRegistrationMapper;
import at.qe.skeleton.model.Userx;
import at.qe.skeleton.services.UserxService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for user registration.
 *
 * <p>
 * Provides an endpoint for creating new customer accounts.
 * Registration is publicly accessible and does not require authentication.
 * </p>
 */
@RestController
@RequestMapping("/registration")
public class RegistrationController {

    private final UserxService userxService;
    private final UserxRegistrationMapper userxRegistrationMapper;
    private final UserxMeMapper userxMeMapper;

    public RegistrationController(UserxService userxService, UserxRegistrationMapper userxRegistrationMapper, UserxMeMapper userxMeMapper) {
        this.userxService = userxService;
        this.userxRegistrationMapper = userxRegistrationMapper;
        this.userxMeMapper = userxMeMapper;
    }

    /**
     * Registers a new customer account.
     *
     * <p>Possible responses:</p>
     * <ul>
     *   <li>201 Created - user successfully registered</li>
     *   <li>400 Bad Request - validation failed or username already exists</li>
     * </ul>
     *
     * @param dto registration data
     * @return the newly registered user
     */
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public UserxMeDTO register(@Valid @RequestBody UserxRegistrationDTO dto) {
        Userx newCustomer = userxRegistrationMapper.mapFrom(dto);
        return userxMeMapper.mapTo(userxService.registerCustomer(newCustomer));
    }
}
