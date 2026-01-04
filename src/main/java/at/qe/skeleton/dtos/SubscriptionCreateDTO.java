package at.qe.skeleton.dtos;

import at.qe.skeleton.model.SubscriptionType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record SubscriptionCreateDTO(
        @NotNull(message = "Product Id is required")
        Long productId,

        @NotEmpty(message = "At least one subscription type must be selected")
        Set<SubscriptionType> types
) {
}
