package at.qe.skeleton.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserxRegistrationDTO(
        @NotBlank @Size(min = 3, max = 50)
        String username,

        @NotBlank @Size(min = 8, max = 72)
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).*$",
                message = "Password must contain upper, lower case letters and a number"
        )
        String password,

        @NotBlank @Size(max = 50)
        String firstName,

        @NotBlank @Size(max = 50)
        String lastName,

        @NotBlank @Email @Size(max = 100)
        String email,

        @Size(max = 20)
        String phone
) {}
