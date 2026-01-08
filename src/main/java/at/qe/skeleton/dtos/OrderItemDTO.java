package at.qe.skeleton.dtos;

public record OrderItemDTO(
        Long productId,
        Integer quantity,
        Double priceAtPurchase,
        Double appliedDiscount
) {}
