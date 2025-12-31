package at.qe.skeleton.dtos;

import jakarta.validation.constraints.Positive;


public record CartItemUpdateDTO(
    @Positive
    Integer quantity,
    Double appliedDiscount
) {}

