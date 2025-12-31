package at.qe.skeleton.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;


public record CartItemCreateDTO(
    @NotNull
    @Positive
    Long productId,
    @NotNull
    @Positive
    Integer quantity
) {}

