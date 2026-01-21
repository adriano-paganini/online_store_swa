package at.qe.skeleton.dtos;

import at.qe.skeleton.model.NotificationType;
import at.qe.skeleton.model.SubscriptionType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

/**
 * Data Transfer Object used to capture necessary information for creating a new subscription.
 * Requires a target product and at least one subscription type and notification channel.
 */
public record SubscriptionCreateDTO(
        @NotNull(message = "Product Id is required")
        Long productId,

        @NotEmpty(message = "At least one subscription type must be selected")
        Set<SubscriptionType> types,

        @NotEmpty(message = "At least one notification channel must be selected")
        Set<NotificationType> channels
) {
}