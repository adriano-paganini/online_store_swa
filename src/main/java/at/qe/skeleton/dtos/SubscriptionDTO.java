package at.qe.skeleton.dtos;

import at.qe.skeleton.model.NotificationType;
import at.qe.skeleton.model.SubscriptionType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.Set;

public record SubscriptionDTO(
        @NotNull(message = "Subscription ID must be provided")
        @Positive(message = "Subscription ID must be a positive number")
        Long id,

        @NotNull(message = "User ID is required")
        @Positive(message = "User ID must be a positive number")
        Long userId,

        @NotNull(message = "Product ID is required")
        @Positive(message = "Product ID must be a positive number")
        Long productId,

        @NotEmpty(message = "Subscription must contain at least one type (e.g., EMAIL, SMS)")
        Set<SubscriptionType> types,

        @NotEmpty(message = "At leas one notification channel must be selected")
        Set<NotificationType> channels
) {
}
