package at.qe.skeleton.dtos;

import at.qe.skeleton.model.NotificationType;
import at.qe.skeleton.model.UserxRole;

import java.util.Set;

public record UserxMeResponseDTO(
        String username,
        String firstName,
        String lastName,
        String email,
        String phone,
        UserxRole role,
        Set<NotificationType> channels
) {}
