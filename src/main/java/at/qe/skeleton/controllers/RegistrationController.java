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

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public UserxMeDTO register(@Valid @RequestBody UserxRegistrationDTO dto) {
        Userx newCustomer = userxRegistrationMapper.mapFrom(dto);
        return userxMeMapper.mapTo(userxService.registerCustomer(newCustomer));
    }
}
