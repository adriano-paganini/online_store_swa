package at.qe.skeleton.dtos;

public record OrderItemDTO(
        Long productId,
        String productName,
        Integer quantity,
        Double priceAtPurchase,
        Double appliedDiscount
) {}
