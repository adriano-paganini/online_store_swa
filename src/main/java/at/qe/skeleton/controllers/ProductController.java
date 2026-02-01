package at.qe.skeleton.controllers;

import at.qe.skeleton.dtos.PageResponseDTO;
import at.qe.skeleton.dtos.ProductCreateDTO;
import at.qe.skeleton.dtos.ProductDTO;
import at.qe.skeleton.dtos.ProductUpdateDTO;
import at.qe.skeleton.mappers.ProductCreateMapper;
import at.qe.skeleton.mappers.ProductMapper;
import at.qe.skeleton.model.Product;
import at.qe.skeleton.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST controller for accessing and managing products.
 *
 * <p>
 * Provides public endpoints for retrieving product information, as well as
 * restricted endpoints for creating, updating, and deleting products.
 * The level of detail in product representations may vary depending on
 * the authentication state and user authorities.
 * </p>
 */
@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final ProductMapper productMapper;
    private final ProductCreateMapper productCreateMapper;

    public ProductController(ProductService productService, ProductMapper productMapper, ProductCreateMapper productCreateMapper) {
        this.productService = productService;
        this.productMapper = productMapper;
        this.productCreateMapper = productCreateMapper;
    }

    /**
     * Retrieves a paginated list of products.
     *
     * <p>Supports filtering by price range, stock availability, rating, and
     * full-text search, as well as pagination and sorting.</p>
     *
     * <p>Possible responses:</p>
     * <ul>
     *   <li>200 OK - products successfully retrieved</li>
     *   <li>400 Bad Request - invalid query parameters</li>
     * </ul>
     *
     * @return paginated list of products
     */
    @GetMapping("")
    public ResponseEntity<PageResponseDTO<ProductDTO>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int limit,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Boolean inStock,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String search) {

        try {
            Page<Product> productPage = productService.getAllProducts(
                    page, limit, minPrice, maxPrice, inStock, minRating, sort, search);

            final Collection<? extends GrantedAuthority> authorities = getAuthorities();

            List<ProductDTO> productDTOs = productPage.getContent().stream()
                    .map(product -> productMapper.mapTo(product, authorities))
                    .collect(Collectors.toList());

            PageResponseDTO<ProductDTO> response = new PageResponseDTO<>(
                    productDTOs,
                    page,
                    limit,
                    productPage.getTotalElements(),
                    productPage.getTotalPages()
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error fetching products: " + e.getMessage());
        }
    }

    /**
     * Retrieves a single product by its identifier.
     *
     * <p>Possible responses:</p>
     * <ul>
     *   <li>200 OK - product successfully retrieved</li>
     *   <li>404 Not Found - product does not exist</li>
     * </ul>
     *
     * @param id identifier of the product
     * @return the requested product
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        try {
            Optional<Product> product = productService.getProductById(id);
            if (product.isPresent()) {
                final Collection<? extends GrantedAuthority> authorities = getAuthorities();
                ProductDTO productDTO = productMapper.mapTo(product.get(), authorities);
                return ResponseEntity.ok(productDTO);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error fetching product: " + e.getMessage());
        }
    }


    /**
     * Creates a new product.
     *
     * <p>Possible responses:</p>
     * <ul>
     *   <li>201 Created - product successfully created</li>
     *   <li>400 Bad Request - validation failed</li>
     * </ul>
     *
     * @param createDTO product data to create
     * @return the newly created product
     */
    @PostMapping("")
    public ResponseEntity<ProductDTO> createProduct(
            @Valid @RequestBody ProductCreateDTO createDTO) {

        try {
            Product product = productCreateMapper.mapFrom(createDTO);
            Product createdProduct = productService.createProduct(product);
            final Collection<? extends GrantedAuthority> authorities = getAuthorities();
            ProductDTO productDTO = productMapper.mapTo(createdProduct, authorities);
            return ResponseEntity.status(HttpStatus.CREATED).body(productDTO);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error creating product: " + e.getMessage());
        }
    }

    /**
     * Updates an existing product.
     *
     * <p>Possible responses:</p>
     * <ul>
     *   <li>200 OK - product successfully updated</li>
     *   <li>400 Bad Request - invalid update data</li>
     *   <li>404 Not Found - product does not exist</li>
     * </ul>
     *
     * @param id identifier of the product
     * @param updateDTO updated product data
     * @return the updated product
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductUpdateDTO updateDTO) {

        try {
            Product product = productService.updateProduct(id, updateDTO);
            final Collection<? extends GrantedAuthority> authorities = getAuthorities();
            ProductDTO productDTO = productMapper.mapTo(product, authorities);
            return ResponseEntity.ok(productDTO);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error updating product: " + e.getMessage());
        }
    }

    /**
     * Deletes a product.
     *
     * <p>The product is soft-deleted and will no longer be visible to users.</p>
     *
     * <p>Possible responses:</p>
     * <ul>
     *   <li>204 No Content - product successfully deleted</li>
     *   <li>404 Not Found - product does not exist</li>
     * </ul>
     *
     * @param id identifier of the product to delete
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        try {
            productService.softDeleteProduct(id);
            return ResponseEntity.noContent().build();
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error deleting product: " + e.getMessage());
        }
    }


    private Collection<? extends GrantedAuthority> getAuthorities() {
        try {
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() && 
                !authentication.getPrincipal().equals("anonymousUser")) {
                return authentication.getAuthorities();
            }
        } catch (Exception e) {
            // No authentication available, use null (public access)
        }
        return Collections.emptyList();
    }
}

