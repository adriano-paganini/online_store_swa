package at.qe.skeleton.dtos;

public record OrderCreateDTO(
        Long shippingAddressId,
        Long billingAddressId
) {}
