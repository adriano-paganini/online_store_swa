package at.qe.skeleton.services;

import at.qe.skeleton.dtos.ProductCreateDTO;
import at.qe.skeleton.dtos.ProductUpdateDTO;
import at.qe.skeleton.model.Product;
import org.springframework.data.domain.Page;

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
    
    Product createProduct(ProductCreateDTO createDTO);
    
    Product updateProduct(Long id, ProductUpdateDTO updateDTO);
    
    void softDeleteProduct(Long id);
}

