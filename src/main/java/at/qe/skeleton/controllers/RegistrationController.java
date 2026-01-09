package at.qe.skeleton.controllers;

import at.qe.skeleton.dtos.UserxMeDTO;
import at.qe.skeleton.dtos.UserxRegistrationDTO;
import at.qe.skeleton.mappers.UserxMapper;
import at.qe.skeleton.services.UserxService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/registration")
public class RegistrationController {


    private final UserxMapper userxMapper;
    private final UserxService userxService;

    public RegistrationController(UserxMapper userxMapper, UserxService userxService) {
        this.userxMapper = userxMapper;
        this.userxService = userxService;
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public UserxMeDTO register(@Valid @RequestBody UserxRegistrationDTO dto) {
        return userxMapper.mapToMe(userxService.registerCustomer(dto));
    }
}
