package at.qe.skeleton.dtos;

import at.qe.skeleton.model.NotificationType;
import at.qe.skeleton.model.UserxRole;
import jakarta.validation.constraints.*;

import java.util.Set;

public record UserxUpdateDTO(
        @Size(min = 3, max = 50)
        String username,

        @Size(min = 8, max = 72)
        String password,

        @Size(max = 50)
        String firstName,

        @Size(max = 50)
        String lastName,

        @Email @Size(max = 100)
        String email,

        @Size(max = 20)
        String phone,

        Set<UserxRole> roles,

        Set<NotificationType> channels
) {}
