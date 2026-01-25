package at.qe.skeleton.services;

import at.qe.skeleton.dtos.ProductUpdateDTO;
import at.qe.skeleton.model.Product;
import org.springframework.data.domain.Page;

import java.util.Map;
import java.util.Optional;

public interface ProductService {
    // Existing methods for backward compatibility
    Optional<Double> getProductPrice(Long productId);
    Optional<Double> getProductDiscount(Long productId);
    boolean isProductAvailable(Long productId, Integer quantity);
    void updateProductAverageScore(Long productId, Double averageScore);
    
    // New CRUD operations
    Page<Product> getAllProducts(
            int page,
            int limit,
            Double minPrice,
            Double maxPrice,
            Boolean inStock,
            Double minRating,
            String sort
    );
    
    Optional<Product> getProductById(Long id);
    
    Product createProduct(Product product);
    
    Product updateProduct(Long id, ProductUpdateDTO updateDTO);

    void adjustProductStockWithMap(Map<Long,Integer> items);

    void softDeleteProduct(Long id);
}

