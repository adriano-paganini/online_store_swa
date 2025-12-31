package at.qe.skeleton.services;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

/**
 * Temporary implementation of ProductService.
 * This is a placeholder that returns mock values matching the frontend mock data.
 * Should be replaced with actual Product entity implementation.
 */
@Service
public class ProductServiceImpl implements ProductService {

    // Mock product prices matching frontend mock data
    private static final Map<Long, Double> MOCK_PRICES = Map.of(
            1L, 199.99,
            2L, 129.99,
            3L, 59.99,
            4L, 399.99,
            5L, 199.99,
            6L, 129.99,
            7L, 59.99,
            8L, 399.99,
            9L, 249.99,
            10L, 89.99
    );

    // Mock product discounts matching frontend mock data
    private static final Map<Long, Double> MOCK_DISCOUNTS = Map.of(
            3L, 0.1,  // 10% discount
            5L, 0.2,  // 20% discount
            8L, 0.15  // 15% discount
    );

    // Mock stock levels matching frontend mock data
    private static final Map<Long, Integer> MOCK_STOCK = Map.of(
            1L, 15,
            2L, 0,   // Out of stock
            3L, 9,
            4L, 5,
            5L, 15,
            6L, 0,   // Out of stock
            7L, 9,
            8L, 5,
            9L, 20,
            10L, 12
    );

    @Override
    public Optional<Double> getProductPrice(Long productId) {
        // TODO: Implement when Product entity is created
        // For now, return mock price matching frontend data
        return Optional.of(MOCK_PRICES.getOrDefault(productId, 99.99));
    }

    @Override
    public Optional<Double> getProductDiscount(Long productId) {
        // TODO: Implement when Product entity is created
        // For now, return mock discount matching frontend data
        return Optional.of(MOCK_DISCOUNTS.getOrDefault(productId, 0.0));
    }

    @Override
    public boolean isProductAvailable(Long productId, Integer quantity) {
        // TODO: Implement when Product entity is created
        // For now, check mock stock levels
        Integer stock = MOCK_STOCK.getOrDefault(productId, 10);
        return stock >= quantity;
    }

    @Override
    public void updateProductAverageScore(Long productId, Double averageScore) {
        // TODO: Implement when Product entity is created
        // For now, this is a placeholder that does nothing
        // When Product entity exists, update: product.setAvgScore(averageScore)
    }
}

