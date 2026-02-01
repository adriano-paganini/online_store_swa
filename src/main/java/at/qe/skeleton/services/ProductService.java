package at.qe.skeleton.services;

import at.qe.skeleton.dtos.ProductUpdateDTO;
import at.qe.skeleton.model.Product;
import org.springframework.data.domain.Page;

import java.util.Map;
import java.util.Optional;

/**
 * Product service interface.
 * <p>
 * Handles core business logic for products, including creation, update,
 * deletion, and filtered retrieval.
 */
public interface ProductService {
    // Existing methods for backward compatibility
    /**
     * Retrieves the price of a product.
     *
     * @param productId the id of the product
     * @return the price of the product or an empty {@code Optional}
     */
    Optional<Double> getProductPrice(Long productId);

    /**
     * Retrieves the discount of a product.
     *
     * @param productId the id of the product
     * @return the discount of the product or an empty {@code Optional}
     */
    Optional<Double> getProductDiscount(Long productId);

    /**
     * Checks whether the requested quantity of a product is available in stock.
     *
     * @param productId the id of the product to check
     * @param quantity the required quantity
     * @return {@code true} if sufficient stock is available, {@code false} otherwise
     */
    boolean isProductAvailable(Long productId, Integer quantity);

    /**
     * Updates the product's average score.
     *
     * @param productId the id of the product to update
     * @param averageScore the new average score
     */
    void updateProductAverageScore(Long productId, Double averageScore);
    
    // New CRUD operations

    /**
     * Retrieves a paginated list of all products with optional filtering and sorting.
     *
     * @param page the page index
     * @param limit the maximum number of products per page
     * @param minPrice optional filter by minimum price
     * @param maxPrice optional filter by maximum price
     * @param inStock optional filter if the product is in stock
     * @param minRating optional filter by minimum rating
     * @param sort sort specification
     * @param search search specification
     * @return a page of products matching the given criteria
     */
    Page<Product> getAllProducts(
            int page,
            int limit,
            Double minPrice,
            Double maxPrice,
            Boolean inStock,
            Double minRating,
            String sort,
            String search
    );

    /**
     * Retrieves a product by id.
     *
     * @param id the id of the product
     * @return the product or an empty {@code Optional}
     */
    Optional<Product> getProductById(Long id);

    /**
     * Creates a new product.
     *
     * @param product the new product
     * @return the saved product
     */
    Product createProduct(Product product);

    /**
     * Updates a product.
     *
     * @param id the id of the product to update
     * @param updateDTO the UpdateDTO with the updated fields
     * @return the updated product
     */
    Product updateProduct(Long id, ProductUpdateDTO updateDTO);

    /**
     * Adjusts product stock levels based on the given product–quantity map.
     *
     * @param items map of product IDs to required quantities
     */
    void adjustProductStockWithMap(Map<Long,Integer> items);

    /**
     * Deletes a product (soft delete).
     *
     * @param id the id of the product to delete
     */
    void softDeleteProduct(Long id);
}

