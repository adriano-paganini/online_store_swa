package at.qe.skeleton.dtos;

import at.qe.skeleton.model.NotificationType;
import at.qe.skeleton.model.SubscriptionType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

/**
 * Data Transfer Object used for updating the configurations of an existing subscription.
 * Allows users to modify their preferred subscription types and notification channels.
 */
public record SubscriptionUpdateDTO(
        @NotEmpty(message = "Subscription must have at least one type")
        @NotNull(message = "Types list cannot be null")
        Set<SubscriptionType> types,

        @NotEmpty(message = "At least one notification channel must be selected")
        @NotNull(message = "Channel list cannot be null")
        Set<NotificationType> channels
) {
}