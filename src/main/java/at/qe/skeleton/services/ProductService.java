package at.qe.skeleton.services;

import java.util.Optional;


public interface ProductService {
    Optional<Double> getProductPrice(Long productId);
    Optional<Double> getProductDiscount(Long productId);
    boolean isProductAvailable(Long productId, Integer quantity);
    
    /**
     * Updates the average score for a product.
     * This is a placeholder that will be fully implemented when Product entity is created.
     *
     * @param productId the product ID
     * @param averageScore the new average score
     */
    void updateProductAverageScore(Long productId, Double averageScore);
}

