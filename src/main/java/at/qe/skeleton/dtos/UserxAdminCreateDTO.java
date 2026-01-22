package at.qe.skeleton.dtos;

import at.qe.skeleton.model.UserxRole;
import jakarta.validation.constraints.*;

import java.util.Set;

/**
 * Reduced data transfer object for the UserxTypes Entity in the create endpoint.
 * This class is part of the skeleton project provided for students of the course "Software
 * Architecture" offered by Innsbruck University.
 */
public record UserxAdminCreateDTO(
        @NotBlank @Size(min = 3, max = 50)
        String username,

        @NotBlank @Size(min = 8, max = 72)
        String password,

        @NotBlank @Size(max = 50)
        String firstName,

        @NotBlank @Size(max = 50)
        String lastName,

        @NotBlank @Email @Size(max = 100)
        String email,

        @Size(max = 20)
        String phone,

        @NotNull
        Boolean enabled,

        @NotEmpty
        Set<UserxRole> roles
) {}
