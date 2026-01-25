package at.qe.skeleton.dtos;

import at.qe.skeleton.model.UserxRole;

import java.util.Set;

public record UserxMeDTO(
        String username,
        String firstName,
        String lastName,
        String email,
        String phone,
        Set<UserxRole> roles
) {}
