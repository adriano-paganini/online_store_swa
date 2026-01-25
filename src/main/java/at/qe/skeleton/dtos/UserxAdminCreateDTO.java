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

        @NotBlank(message = "Username must not be blank")
        @Size(
                min = 3,
                max = 50,
                message = "Username must be between 3 and 50 characters long"
        )
        String username,

        @NotBlank(message = "Password must not be blank")
        @Size(
                min = 8,
                max = 72,
                message = "Password must be between 8 and 72 characters long"
        )
        String password,

        @NotBlank(message = "First name must not be blank")
        @Size(
                max = 50,
                message = "First name must not exceed 50 characters"
        )
        String firstName,

        @NotBlank(message = "Last name must not be blank")
        @Size(
                max = 50,
                message = "Last name must not exceed 50 characters"
        )
        String lastName,

        @NotBlank(message = "Email address must not be blank")
        @Email(message = "Email address must be a valid email")
        @Size(
                max = 100,
                message = "Email address must not exceed 100 characters"
        )
        String email,

        @Size(
                max = 20,
                message = "Phone number must not exceed 20 characters"
        )
        String phone,

        @NotEmpty(message = "At least one role must be assigned to the user")
        Set<UserxRole> roles
) {}
