package at.qe.skeleton.dtos;

import at.qe.skeleton.model.NotificationStatus;
import at.qe.skeleton.model.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record NotificationDTO(
        Long id,

        @NotNull(message = "User ID is required")
        Long userId,

        @NotBlank(message = "Message content cannot be empty")
        @Size(max = 500, message = "Message is too long")
        String message,

        @NotNull(message = "Notification channel/type is required")
        NotificationType channel,

        @NotNull(message = "Status is required")
        NotificationStatus status,

        @PastOrPresent(message = "Timestamp cannot be in the future")
        LocalDateTime timestamp
) {
}