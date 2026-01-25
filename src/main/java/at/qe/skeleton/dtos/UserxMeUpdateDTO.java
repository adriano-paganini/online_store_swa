package at.qe.skeleton.dtos;

import jakarta.validation.constraints.*;

public record UserxMeUpdateDTO(

        @NotNull(message = "Username must be provided.")
        @Size(
                min = 3,
                max = 50,
                message = "Username must be between 3 and 50 characters long.n"
        )
        String username,

        @Size(
                min = 8,
                max = 72,
                message = "Password must be between 8 and 72 characters long."
        )
        String password,

        @NotNull(message = "First name must be provided.")
        @Size(
                max = 50,
                message = "First name must not exceed 50 characters."
        )
        String firstName,

        @NotNull(message = "Last name must be provided.")
        @Size(
                max = 50,
                message = "Last name must not exceed 50 characters."
        )
        String lastName,

        @NotNull(message = "Email address must be provided.")
        @Email(message = "Email address must be a valid email.")
        @Size(
                max = 100,
                message = "Email address must not exceed 100 characters."
        )
        String email,

        @Size(
                max = 20,
                message = "Phone number must not exceed 20 characters."
        )
        String phone
) {}
