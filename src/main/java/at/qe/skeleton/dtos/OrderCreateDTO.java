package at.qe.skeleton.dtos;

import at.qe.skeleton.model.ShippingMethod;

public record OrderCreateDTO(
        Long shippingAddressId,
        Long billingAddressId,
        ShippingMethod shippingMethod
) {}
