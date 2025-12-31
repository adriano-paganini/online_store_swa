package at.qe.skeleton.services;

import java.util.Optional;


public interface ProductService {
    Optional<Double> getProductPrice(Long productId);
    Optional<Double> getProductDiscount(Long productId);
    boolean isProductAvailable(Long productId, Integer quantity);
}

