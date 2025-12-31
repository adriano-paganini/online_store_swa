package at.qe.skeleton.controllers;

import at.qe.skeleton.dtos.PageResponseDTO;
import at.qe.skeleton.dtos.ProductCreateDTO;
import at.qe.skeleton.dtos.ProductDTO;
import at.qe.skeleton.dtos.ProductUpdateDTO;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final ProductMapper productMapper;

    public ProductController(ProductService productService, ProductMapper productMapper) {
        this.productService = productService;
        this.productMapper = productMapper;
    }


    @GetMapping("")
    public ResponseEntity<PageResponseDTO<ProductDTO>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int limit,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Boolean inStock,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) String sort) {

        try {
            Page<Product> productPage = productService.getAllProducts(
                    page, limit, minPrice, maxPrice, inStock, minRating, sort);

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


    @PostMapping("")
    public ResponseEntity<ProductDTO> createProduct(
            @Valid @RequestBody ProductCreateDTO createDTO) {

        try {
            Product product = productService.createProduct(createDTO);
            final Collection<? extends GrantedAuthority> authorities = getAuthorities();
            ProductDTO productDTO = productMapper.mapTo(product, authorities);
            return ResponseEntity.status(HttpStatus.CREATED).body(productDTO);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error creating product: " + e.getMessage());
        }
    }


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
        return null;
    }
}

