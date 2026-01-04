package at.qe.skeleton.dtos;

import at.qe.skeleton.model.SubscriptionType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record SubscriptionUpdateDTO(
        @NotEmpty(message = "Subscription must have at least one type")
        @NotNull(message = "Types list cannot be null")
        Set<SubscriptionType> types) {
}
