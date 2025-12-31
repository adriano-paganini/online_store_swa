package at.qe.skeleton.dtos;


public record CartItemDTO(
    Long id,
    Long productId,
    Integer quantity,
    Double appliedDiscount,
    Double currentPrice
) {}

