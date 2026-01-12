package at.qe.skeleton.tests;

import at.qe.skeleton.dtos.ProductCreateDTO;
import at.qe.skeleton.dtos.ProductUpdateDTO;
import at.qe.skeleton.model.Product;
import at.qe.skeleton.model.Userx;
import at.qe.skeleton.repositories.ProductRepository;
import at.qe.skeleton.services.AuthenticatedUserService;
import at.qe.skeleton.services.ProductService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootTest
public class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @MockitoBean
    private ProductRepository productRepository;

    @MockitoBean
    private AuthenticatedUserService authenticatedUserService;

    private Userx testUser;
    private Product testProduct;
    private Long testProductId;

    @BeforeEach
    void setUp() {
        testProductId = 1L;

        testUser = new Userx();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setFirstName("Test");
        testUser.setLastName("User");

        testProduct = createTestProduct(testProductId, "Test Product", "Test Description", 99.99, 10, 0.1, 4.5, false);

        Mockito.when(authenticatedUserService.getAuthenticatedUser()).thenReturn(testUser);
    }

    @Test
    @WithMockUser(username = "testuser")
    void testGetAllProducts() {
        int page = 0;
        int limit = 12;

        Product product1 = createTestProduct(1L, "Product 1", "Desc 1", 99.99, 10, 0.0, 4.0, false);
        Product product2 = createTestProduct(2L, "Product 2", "Desc 2", 149.99, 5, 0.1, 4.5, false);
        List<Product> products = List.of(product1, product2);
        Page<Product> productPage = new PageImpl<>(products, PageRequest.of(page, limit), 2);

        Mockito.when(productRepository.findAllWithFilters(
                Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull(),
                ArgumentMatchers.any(Pageable.class)))
                .thenReturn(productPage);

        Page<Product> result = productService.getAllProducts(
                page, limit, null, null, null, null, null);

        Assertions.assertNotNull(result, "Result should not be null");
        Assertions.assertEquals(2, result.getTotalElements(), "Should have 2 products");
        Assertions.assertEquals(2, result.getContent().size(), "Should return 2 products");
    }

    @Test
    @WithMockUser(username = "testuser")
    void testGetAllProductsWithMinPriceFilter() {
        int page = 0;
        int limit = 12;
        Double minPrice = 100.0;

        Product product = createTestProduct(2L, "Product 2", "Desc 2", 149.99, 5, 0.1, 4.5, false);
        Page<Product> productPage = new PageImpl<>(List.of(product), PageRequest.of(page, limit), 1);

        Mockito.when(productRepository.findAllWithFilters(
                Mockito.eq(minPrice), Mockito.isNull(), Mockito.isNull(), Mockito.isNull(),
                ArgumentMatchers.any(Pageable.class)))
                .thenReturn(productPage);

        Page<Product> result = productService.getAllProducts(
                page, limit, minPrice, null, null, null, null);

        Assertions.assertNotNull(result, "Result should not be null");
        Assertions.assertEquals(1, result.getTotalElements(), "Should have 1 product");
        Mockito.verify(productRepository).findAllWithFilters(
                Mockito.eq(minPrice), Mockito.isNull(), Mockito.isNull(), Mockito.isNull(),
                ArgumentMatchers.any(Pageable.class));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testGetAllProductsWithMaxPriceFilter() {
        int page = 0;
        int limit = 12;
        Double maxPrice = 100.0;

        Product product = createTestProduct(1L, "Product 1", "Desc 1", 99.99, 10, 0.0, 4.0, false);
        Page<Product> productPage = new PageImpl<>(List.of(product), PageRequest.of(page, limit), 1);

        Mockito.when(productRepository.findAllWithFilters(
                Mockito.isNull(), Mockito.eq(maxPrice), Mockito.isNull(), Mockito.isNull(),
                ArgumentMatchers.any(Pageable.class)))
                .thenReturn(productPage);

        Page<Product> result = productService.getAllProducts(
                page, limit, null, maxPrice, null, null, null);

        Assertions.assertNotNull(result, "Result should not be null");
        Assertions.assertEquals(1, result.getTotalElements(), "Should have 1 product");
    }

    @Test
    @WithMockUser(username = "testuser")
    void testGetAllProductsWithInStockFilter() {
        int page = 0;
        int limit = 12;
        Boolean inStock = true;

        Product product = createTestProduct(1L, "Product 1", "Desc 1", 99.99, 10, 0.0, 4.0, false);
        Page<Product> productPage = new PageImpl<>(List.of(product), PageRequest.of(page, limit), 1);

        Mockito.when(productRepository.findAllWithFilters(
                Mockito.isNull(), Mockito.isNull(), Mockito.eq(inStock), Mockito.isNull(),
                ArgumentMatchers.any(Pageable.class)))
                .thenReturn(productPage);

        Page<Product> result = productService.getAllProducts(
                page, limit, null, null, inStock, null, null);

        Assertions.assertNotNull(result, "Result should not be null");
        Assertions.assertEquals(1, result.getTotalElements(), "Should have 1 product");
    }

    @Test
    @WithMockUser(username = "testuser")
    void testGetAllProductsWithMinRatingFilter() {
        int page = 0;
        int limit = 12;
        Double minRating = 4.5;

        Product product = createTestProduct(2L, "Product 2", "Desc 2", 149.99, 5, 0.1, 4.5, false);
        Page<Product> productPage = new PageImpl<>(List.of(product), PageRequest.of(page, limit), 1);

        Mockito.when(productRepository.findAllWithFilters(
                Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.eq(minRating),
                ArgumentMatchers.any(Pageable.class)))
                .thenReturn(productPage);

        Page<Product> result = productService.getAllProducts(
                page, limit, null, null, null, minRating, null);

        Assertions.assertNotNull(result, "Result should not be null");
        Assertions.assertEquals(1, result.getTotalElements(), "Should have 1 product");
    }

    @Test
    @WithMockUser(username = "testuser")
    void testGetAllProductsWithSort() {
        int page = 0;
        int limit = 12;
        String sort = "price,asc";

        Product product1 = createTestProduct(1L, "Product 1", "Desc 1", 99.99, 10, 0.0, 4.0, false);
        Product product2 = createTestProduct(2L, "Product 2", "Desc 2", 149.99, 5, 0.1, 4.5, false);
        Page<Product> productPage = new PageImpl<>(List.of(product1, product2), PageRequest.of(page, limit), 2);

        Mockito.when(productRepository.findAllWithFilters(
                Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull(),
                ArgumentMatchers.any(Pageable.class)))
                .thenReturn(productPage);

        Page<Product> result = productService.getAllProducts(
                page, limit, null, null, null, null, sort);

        Assertions.assertNotNull(result, "Result should not be null");
        Mockito.verify(productRepository).findAllWithFilters(
                Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull(),
                ArgumentMatchers.any(Pageable.class));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testGetAllProductsWithInvalidSort() {
        int page = 0;
        int limit = 12;
        String sort = "invalidField,desc";

        Product product = createTestProduct(1L, "Product 1", "Desc 1", 99.99, 10, 0.0, 4.0, false);
        Page<Product> productPage = new PageImpl<>(List.of(product), PageRequest.of(page, limit), 1);

        Mockito.when(productRepository.findAllWithFilters(
                Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull(),
                ArgumentMatchers.any(Pageable.class)))
                .thenReturn(productPage);

        Page<Product> result = productService.getAllProducts(
                page, limit, null, null, null, null, sort);

        Assertions.assertNotNull(result, "Result should not be null");
    }

    @Test
    @WithMockUser(username = "testuser")
    void testGetProductById() {
        Mockito.when(productRepository.findByIdAndNotDeleted(testProductId))
                .thenReturn(Optional.of(testProduct));

        Optional<Product> result = productService.getProductById(testProductId);

        Assertions.assertTrue(result.isPresent(), "Product should be found");
        Assertions.assertEquals(testProductId, result.get().getId(), "Product ID should match");
        Assertions.assertEquals("Test Product", result.get().getName(), "Product name should match");
    }

    @Test
    @WithMockUser(username = "testuser")
    void testGetProductByIdNotFound() {
        Long nonExistentId = 999L;
        Mockito.when(productRepository.findByIdAndNotDeleted(nonExistentId))
                .thenReturn(Optional.empty());

        Optional<Product> result = productService.getProductById(nonExistentId);

        Assertions.assertFalse(result.isPresent(), "Product should not be found");
    }

    @Test
    @WithMockUser(username = "testuser")
    void testGetProductByIdDeleted() {
        Mockito.when(productRepository.findByIdAndNotDeleted(testProductId))
                .thenReturn(Optional.empty());

        Optional<Product> result = productService.getProductById(testProductId);

        Assertions.assertFalse(result.isPresent(), "Deleted product should not be found");
    }

    @Test
    @WithMockUser(username = "testuser")
    void testCreateProduct() {
        ProductCreateDTO createDTO = new ProductCreateDTO(
                "New Product",
                "New Description",
                149.99,
                20,
                0.15,
                new ArrayList<>()
        );

        Mockito.when(productRepository.save(Mockito.any(Product.class)))
                .thenAnswer(invocation -> {
                    Product product = invocation.getArgument(0);
                    product.setId(2L);
                    return product;
                });

        Product result = productService.createProduct(createDTO);

        Assertions.assertNotNull(result, "Product should not be null");
        Assertions.assertEquals("New Product", result.getName(), "Name should match");
        Assertions.assertEquals("New Description", result.getDescription(), "Description should match");
        Assertions.assertEquals(149.99, result.getPrice(), "Price should match");
        Assertions.assertEquals(20, result.getStock(), "Stock should match");
        Assertions.assertEquals(0.15, result.getDiscount(), "Discount should match");
        Assertions.assertEquals(0.0, result.getAvgScore(), "AvgScore should be 0.0");
        Assertions.assertFalse(result.getDeleted(), "Product should not be deleted");
        Assertions.assertNotNull(result.getImages(), "Images should not be null");
        Assertions.assertTrue(result.getImages().isEmpty(), "Images should be empty list");

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        Mockito.verify(productRepository).save(productCaptor.capture());
        Product savedProduct = productCaptor.getValue();
        Assertions.assertEquals(testUser, savedProduct.getCreateUser(), "Create user should be set");
    }

    @Test
    @WithMockUser(username = "testuser")
    void testCreateProductWithDefaultDiscount() {
        ProductCreateDTO createDTO = new ProductCreateDTO(
                "New Product",
                "Description",
                99.99,
                10,
                null, // Discount not provided
                new ArrayList<>()
        );

        Mockito.when(productRepository.save(Mockito.any(Product.class)))
                .thenAnswer(invocation -> {
                    Product product = invocation.getArgument(0);
                    product.setId(2L);
                    return product;
                });

        Product result = productService.createProduct(createDTO);

        Assertions.assertEquals(0.0, result.getDiscount(), "Discount should default to 0.0");
    }

    @Test
    @WithMockUser(username = "testuser")
    void testCreateProductWithImages() {
        List<String> images = List.of("https://example.com/image1.jpg", "https://example.com/image2.jpg");
        ProductCreateDTO createDTO = new ProductCreateDTO(
                "New Product",
                "Description",
                99.99,
                10,
                0.0,
                images
        );

        Mockito.when(productRepository.save(Mockito.any(Product.class)))
                .thenAnswer(invocation -> {
                    Product product = invocation.getArgument(0);
                    product.setId(2L);
                    return product;
                });

        Product result = productService.createProduct(createDTO);

        Assertions.assertNotNull(result, "Product should not be null");
        Assertions.assertEquals(images, result.getImages(), "Images should match");
    }

    @Test
    void testCreateProductUnauthenticated() {
        ProductCreateDTO createDTO = new ProductCreateDTO(
                "New Product",
                "Description",
                99.99,
                10,
                0.0,
                new ArrayList<>()
        );

        Mockito.when(authenticatedUserService.getAuthenticatedUser()).thenReturn(null);
        Mockito.when(productRepository.save(Mockito.any(Product.class)))
                .thenAnswer(invocation -> {
                    Product product = invocation.getArgument(0);
                    product.setId(2L);
                    return product;
                });

        Product result = productService.createProduct(createDTO);

        Assertions.assertNotNull(result, "Product should still be created");
        Assertions.assertEquals("New Product", result.getName(), "Name should match");
        // Create user might be null if not authenticated
    }

    @Test
    @WithMockUser(username = "testuser")
    void testUpdateProduct() {
        List<String> newImages = List.of("https://example.com/image1.jpg", "https://example.com/image2.jpg");
        ProductUpdateDTO updateDTO = new ProductUpdateDTO(
                "Updated Product",
                "Updated Description",
                199.99,
                15,
                0.2,
                newImages
        );

        Mockito.when(productRepository.findByIdAndNotDeleted(testProductId))
                .thenReturn(Optional.of(testProduct));
        Mockito.when(productRepository.save(Mockito.any(Product.class)))
                .thenReturn(testProduct);

        Product result = productService.updateProduct(testProductId, updateDTO);

        Assertions.assertNotNull(result, "Product should not be null");
        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        Mockito.verify(productRepository).save(productCaptor.capture());
        Product updatedProduct = productCaptor.getValue();
        Assertions.assertEquals("Updated Product", updatedProduct.getName(), "Name should be updated");
        Assertions.assertEquals("Updated Description", updatedProduct.getDescription(), "Description should be updated");
        Assertions.assertEquals(199.99, updatedProduct.getPrice(), "Price should be updated");
        Assertions.assertEquals(15, updatedProduct.getStock(), "Stock should be updated");
        Assertions.assertEquals(0.2, updatedProduct.getDiscount(), "Discount should be updated");
        Assertions.assertEquals(newImages, updatedProduct.getImages(), "Images should be updated");
        Assertions.assertEquals(testUser, updatedProduct.getUpdateUser(), "Update user should be set");
    }

    @Test
    @WithMockUser(username = "testuser")
    void testUpdateProductPartial() {
        ProductUpdateDTO updateDTO = new ProductUpdateDTO(
                "Updated Name",
                null, // Only update name
                null,
                null,
                null,
                null
        );

        Mockito.when(productRepository.findByIdAndNotDeleted(testProductId))
                .thenReturn(Optional.of(testProduct));
        Mockito.when(productRepository.save(Mockito.any(Product.class)))
                .thenReturn(testProduct);

        Product result = productService.updateProduct(testProductId, updateDTO);

        Assertions.assertNotNull(result, "Product should not be null");
        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        Mockito.verify(productRepository).save(productCaptor.capture());
        Product updatedProduct = productCaptor.getValue();
        Assertions.assertEquals("Updated Name", updatedProduct.getName(), "Name should be updated");
        Assertions.assertEquals("Test Description", updatedProduct.getDescription(), "Description should remain unchanged");
        Assertions.assertEquals(99.99, updatedProduct.getPrice(), "Price should remain unchanged");
    }

    @Test
    @WithMockUser(username = "testuser")
    void testUpdateProductNotFound() {
        Long nonExistentId = 999L;
        ProductUpdateDTO updateDTO = new ProductUpdateDTO(
                "Updated Name",
                null,
                null,
                null,
                null,
                null
        );

        Mockito.when(productRepository.findByIdAndNotDeleted(nonExistentId))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(ResponseStatusException.class, () -> {
            productService.updateProduct(nonExistentId, updateDTO);
        }, "Should throw exception when product not found");

        Mockito.verify(productRepository, Mockito.never()).save(Mockito.any(Product.class));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testSoftDeleteProduct() {
        Mockito.when(productRepository.findByIdAndNotDeleted(testProductId))
                .thenReturn(Optional.of(testProduct));
        Mockito.when(productRepository.save(Mockito.any(Product.class)))
                .thenReturn(testProduct);

        productService.softDeleteProduct(testProductId);

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        Mockito.verify(productRepository).save(productCaptor.capture());
        Product deletedProduct = productCaptor.getValue();
        Assertions.assertTrue(deletedProduct.getDeleted(), "Product should be marked as deleted");
    }

    @Test
    @WithMockUser(username = "testuser")
    void testSoftDeleteProductNotFound() {
        Long nonExistentId = 999L;

        Mockito.when(productRepository.findByIdAndNotDeleted(nonExistentId))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(ResponseStatusException.class, () -> {
            productService.softDeleteProduct(nonExistentId);
        }, "Should throw exception when product not found");

        Mockito.verify(productRepository, Mockito.never()).save(Mockito.any(Product.class));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testGetProductPrice() {
        Mockito.when(productRepository.findById(testProductId))
                .thenReturn(Optional.of(testProduct));

        Optional<Double> result = productService.getProductPrice(testProductId);

        Assertions.assertTrue(result.isPresent(), "Price should be found");
        Assertions.assertEquals(99.99, result.get(), "Price should match");
    }

    @Test
    @WithMockUser(username = "testuser")
    void testGetProductDiscount() {
        Mockito.when(productRepository.findById(testProductId))
                .thenReturn(Optional.of(testProduct));

        Optional<Double> result = productService.getProductDiscount(testProductId);

        Assertions.assertTrue(result.isPresent(), "Discount should be found");
        Assertions.assertEquals(0.1, result.get(), "Discount should match");
    }

    @Test
    @WithMockUser(username = "testuser")
    void testIsProductAvailable() {
        Mockito.when(productRepository.findById(testProductId))
                .thenReturn(Optional.of(testProduct));

        boolean result = productService.isProductAvailable(testProductId, 5);

        Assertions.assertTrue(result, "Product should be available");
    }

    @Test
    @WithMockUser(username = "testuser")
    void testIsProductAvailableInsufficientStock() {
        Mockito.when(productRepository.findById(testProductId))
                .thenReturn(Optional.of(testProduct));

        boolean result = productService.isProductAvailable(testProductId, 20);

        Assertions.assertFalse(result, "Product should not be available");
    }

    @Test
    @WithMockUser(username = "testuser")
    void testIsProductAvailableDeleted() {
        Product deletedProduct = createTestProduct(testProductId, "Product", "Desc", 99.99, 10, 0.0, 4.0, true);
        Mockito.when(productRepository.findById(testProductId))
                .thenReturn(Optional.of(deletedProduct));

        boolean result = productService.isProductAvailable(testProductId, 5);

        Assertions.assertFalse(result, "Deleted product should not be available");
    }

    @Test
    @WithMockUser(username = "testuser")
    void testUpdateProductAverageScore() {
        Double newAverageScore = 4.8;

        Mockito.when(productRepository.findById(testProductId))
                .thenReturn(Optional.of(testProduct));
        Mockito.when(productRepository.save(Mockito.any(Product.class)))
                .thenReturn(testProduct);

        productService.updateProductAverageScore(testProductId, newAverageScore);

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        Mockito.verify(productRepository).save(productCaptor.capture());
        Product updatedProduct = productCaptor.getValue();
        Assertions.assertEquals(newAverageScore, updatedProduct.getAvgScore(), "Average score should be updated");
    }

    @Test
    @WithMockUser(username = "testuser")
    void testUpdateProductAverageScoreNotFound() {
        Long nonExistentId = 999L;
        Double newAverageScore = 4.8;

        Mockito.when(productRepository.findById(nonExistentId))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(ResponseStatusException.class, () -> {
            productService.updateProductAverageScore(nonExistentId, newAverageScore);
        }, "Should throw exception when product not found");
    }

    private Product createTestProduct(Long id, String name, String description, Double price,
                                      Integer stock, Double discount, Double avgScore, Boolean deleted) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setStock(stock);
        product.setDiscount(discount);
        product.setAvgScore(avgScore);
        product.setDeleted(deleted);
        product.setImages(new java.util.ArrayList<>());
        return product;
    }
}

