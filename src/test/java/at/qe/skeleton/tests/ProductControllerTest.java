package at.qe.skeleton.tests;

import at.qe.skeleton.configs.JwtConfig;
import at.qe.skeleton.configs.JwtTokenProvider;
import at.qe.skeleton.configs.TokenAuthenticationFilter;
import at.qe.skeleton.controllers.ProductController;
import at.qe.skeleton.dtos.ProductCreateDTO;
import at.qe.skeleton.dtos.ProductDTO;
import at.qe.skeleton.dtos.ProductUpdateDTO;
import at.qe.skeleton.mappers.ProductMapper;
import at.qe.skeleton.model.Product;
import at.qe.skeleton.services.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoSpyBean
    private TokenAuthenticationFilter tokenAuthenticationFilter;

    @MockitoBean
    private JwtConfig jwtConfig;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private ProductMapper productMapper;

    private Product testProduct;
    private ProductDTO testProductDTO;
    private Long testProductId;

    @BeforeEach
    void setUp() throws Exception {
        testProductId = 1L;

        testProduct = new Product();
        testProduct.setId(testProductId);
        testProduct.setName("Test Product");
        testProduct.setDescription("Test Description");
        testProduct.setPrice(99.99);
        testProduct.setStock(10);
        testProduct.setDiscount(0.1);
        testProduct.setAvgScore(4.5);
        testProduct.setImages(new ArrayList<>());
        testProduct.setDeleted(false);

        testProductDTO = new ProductDTO(
                testProductId,
                "Test Product",
                "Test Description",
                99.99,
                10,
                0.1,
                4.5,
                new ArrayList<>(),
                false,
                null, null, null, null
        );

        Mockito.doAnswer(invocation -> {
            HttpServletRequest request = invocation.getArgument(0);
            HttpServletResponse response = invocation.getArgument(1);
            FilterChain chain = invocation.getArgument(2);
            chain.doFilter(request, response);
            return null;
        }).when(tokenAuthenticationFilter).doFilterInternal(
                Mockito.any(HttpServletRequest.class),
                Mockito.any(HttpServletResponse.class),
                Mockito.any(FilterChain.class)
        );

        @SuppressWarnings("unchecked")
        Jws<Claims> mockJws = (Jws<Claims>) Mockito.mock(Jws.class);
        Claims mockClaims = Mockito.mock(Claims.class);
        Mockito.when(mockClaims.getSubject()).thenReturn("testuser");
        Mockito.when(mockJws.getPayload()).thenReturn(mockClaims);
        Mockito.when(jwtTokenProvider.validateTokenAndGetJws(Mockito.anyString()))
                .thenReturn(Optional.of(mockJws));
    }

    @Test
    void getAllProductsPublic() throws Exception {
        int page = 0;
        int limit = 12;
        PageRequest pageable = PageRequest.of(page, limit);
        Page<Product> productPage = new PageImpl<>(List.of(testProduct), pageable, 1);

        Mockito.when(productService.getAllProducts(page, limit, null, null, null, null, null))
                .thenReturn(productPage);
        Mockito.when(productMapper.mapTo(Mockito.any(Product.class), Mockito.isNull()))
                .thenReturn(testProductDTO);

        mockMvc.perform(MockMvcRequestBuilders.get("/products"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].id").value(testProductId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].name").value("Test Product"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.page").value(page))
                .andExpect(MockMvcResultMatchers.jsonPath("$.limit").value(limit))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalPages").value(1));
    }

    @Test
    void getAllProductsWithFilters() throws Exception {
        int page = 0;
        int limit = 12;
        Double minPrice = 50.0;
        Double maxPrice = 150.0;
        Boolean inStock = true;
        Double minRating = 4.0;
        String sort = "price,asc";

        PageRequest pageable = PageRequest.of(page, limit);
        Page<Product> productPage = new PageImpl<>(List.of(testProduct), pageable, 1);

        Mockito.when(productService.getAllProducts(page, limit, minPrice, maxPrice, inStock, minRating, sort))
                .thenReturn(productPage);
        Mockito.when(productMapper.mapTo(Mockito.any(Product.class), Mockito.isNull()))
                .thenReturn(testProductDTO);

        mockMvc.perform(MockMvcRequestBuilders.get("/products")
                        .param("minPrice", String.valueOf(minPrice))
                        .param("maxPrice", String.valueOf(maxPrice))
                        .param("inStock", String.valueOf(inStock))
                        .param("minRating", String.valueOf(minRating))
                        .param("sort", sort))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.length()").value(1));
    }

    @Test
    void getAllProductsWithPagination() throws Exception {
        int page = 1;
        int limit = 6;
        PageRequest pageable = PageRequest.of(page, limit);
        Page<Product> productPage = new PageImpl<>(List.of(), pageable, 0);

        Mockito.when(productService.getAllProducts(page, limit, null, null, null, null, null))
                .thenReturn(productPage);

        mockMvc.perform(MockMvcRequestBuilders.get("/products")
                        .param("page", String.valueOf(page))
                        .param("limit", String.valueOf(limit)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.page").value(page))
                .andExpect(MockMvcResultMatchers.jsonPath("$.limit").value(limit))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(0));
    }

    @Test
    void getAllProductsEmpty() throws Exception {
        int page = 0;
        int limit = 12;
        PageRequest pageable = PageRequest.of(page, limit);
        Page<Product> productPage = new PageImpl<>(List.of(), pageable, 0);

        Mockito.when(productService.getAllProducts(page, limit, null, null, null, null, null))
                .thenReturn(productPage);

        mockMvc.perform(MockMvcRequestBuilders.get("/products"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.length()").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(0));
    }

    @Test
    void getProductByIdPublic() throws Exception {
        Mockito.when(productService.getProductById(testProductId))
                .thenReturn(Optional.of(testProduct));
        Mockito.when(productMapper.mapTo(testProduct, null))
                .thenReturn(testProductDTO);

        mockMvc.perform(MockMvcRequestBuilders.get("/products/{id}", testProductId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(testProductId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Test Product"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.price").value(99.99))
                .andExpect(MockMvcResultMatchers.jsonPath("$.stock").value(10));
    }

    @Test
    void getProductByIdNotFound() throws Exception {
        Long nonExistentId = 999L;
        Mockito.when(productService.getProductById(nonExistentId))
                .thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/products/{id}", nonExistentId))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void getProductByIdWithAdminFields() throws Exception {
        Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ADMIN"));
        ProductDTO adminProductDTO = new ProductDTO(
                testProductId,
                "Test Product",
                "Test Description",
                99.99,
                10,
                0.1,
                4.5,
                new ArrayList<>(),
                false,
                1L,
                LocalDateTime.now(),
                1L,
                LocalDateTime.now()
        );

        Mockito.when(productService.getProductById(testProductId))
                .thenReturn(Optional.of(testProduct));
        Mockito.when(productMapper.mapTo(testProduct, authorities))
                .thenReturn(adminProductDTO);

        mockMvc.perform(MockMvcRequestBuilders.get("/products/{id}", testProductId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(testProductId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdByName").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt").exists());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void createProduct() throws Exception {
        ProductCreateDTO createDTO = new ProductCreateDTO(
                "New Product",
                "New Description",
                149.99,
                20,
                0.15
        );

        Product newProduct = new Product();
        newProduct.setId(2L);
        newProduct.setName(createDTO.name());
        newProduct.setDescription(createDTO.description());
        newProduct.setPrice(createDTO.price());
        newProduct.setStock(createDTO.stock());
        newProduct.setDiscount(createDTO.discount());

        ProductDTO newProductDTO = new ProductDTO(
                2L,
                "New Product",
                "New Description",
                149.99,
                20,
                0.15,
                0.0,
                new ArrayList<>(),
                false,
                null, null, null, null
        );

        Mockito.when(productService.createProduct(createDTO)).thenReturn(newProduct);
        Mockito.when(productMapper.mapTo(newProduct, Mockito.any()))
                .thenReturn(newProductDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/products")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(createDTO)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(2L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("New Product"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.price").value(149.99))
                .andExpect(MockMvcResultMatchers.jsonPath("$.stock").value(20));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void createProductInvalidName() throws Exception {
        ProductCreateDTO createDTO = new ProductCreateDTO(
                "", // Invalid: empty name
                "Description",
                99.99,
                10,
                0.0
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/products")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(createDTO)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void createProductInvalidPrice() throws Exception {
        ProductCreateDTO createDTO = new ProductCreateDTO(
                "Product",
                "Description",
                -10.0, // Invalid: negative price
                10,
                0.0
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/products")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(createDTO)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void createProductInvalidStock() throws Exception {
        ProductCreateDTO createDTO = new ProductCreateDTO(
                "Product",
                "Description",
                99.99,
                -5, // Invalid: negative stock
                0.0
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/products")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(createDTO)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void createProductUnauthenticated() throws Exception {
        ProductCreateDTO createDTO = new ProductCreateDTO(
                "New Product",
                "Description",
                99.99,
                10,
                0.0
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/products")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(createDTO)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void updateProduct() throws Exception {
        ProductUpdateDTO updateDTO = new ProductUpdateDTO(
                "Updated Product",
                "Updated Description",
                199.99,
                15,
                0.2
        );

        Product updatedProduct = new Product();
        updatedProduct.setId(testProductId);
        updatedProduct.setName(updateDTO.name());
        updatedProduct.setDescription(updateDTO.description());
        updatedProduct.setPrice(updateDTO.price());
        updatedProduct.setStock(updateDTO.stock());
        updatedProduct.setDiscount(updateDTO.discount());

        ProductDTO updatedProductDTO = new ProductDTO(
                testProductId,
                "Updated Product",
                "Updated Description",
                199.99,
                15,
                0.2,
                4.5,
                new ArrayList<>(),
                false,
                null, null, null, null
        );

        Mockito.when(productService.updateProduct(testProductId, updateDTO)).thenReturn(updatedProduct);
        Mockito.when(productMapper.mapTo(updatedProduct, Mockito.any()))
                .thenReturn(updatedProductDTO);

        mockMvc.perform(MockMvcRequestBuilders.put("/products/{id}", testProductId)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Updated Product"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.price").value(199.99))
                .andExpect(MockMvcResultMatchers.jsonPath("$.stock").value(15));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void updateProductPartial() throws Exception {
        ProductUpdateDTO updateDTO = new ProductUpdateDTO(
                "Updated Name",
                null, // Only update name
                null,
                null,
                null
        );

        Product updatedProduct = new Product();
        updatedProduct.setId(testProductId);
        updatedProduct.setName("Updated Name");
        updatedProduct.setDescription(testProduct.getDescription());
        updatedProduct.setPrice(testProduct.getPrice());
        updatedProduct.setStock(testProduct.getStock());
        updatedProduct.setDiscount(testProduct.getDiscount());

        ProductDTO updatedProductDTO = new ProductDTO(
                testProductId,
                "Updated Name",
                testProduct.getDescription(),
                testProduct.getPrice(),
                testProduct.getStock(),
                testProduct.getDiscount(),
                testProduct.getAvgScore(),
                new ArrayList<>(),
                false,
                null, null, null, null
        );

        Mockito.when(productService.updateProduct(testProductId, updateDTO)).thenReturn(updatedProduct);
        Mockito.when(productMapper.mapTo(updatedProduct, Mockito.any()))
                .thenReturn(updatedProductDTO);

        mockMvc.perform(MockMvcRequestBuilders.put("/products/{id}", testProductId)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Updated Name"));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void updateProductNotFound() throws Exception {
        Long nonExistentId = 999L;
        ProductUpdateDTO updateDTO = new ProductUpdateDTO(
                "Updated Name",
                null,
                null,
                null,
                null
        );

        Mockito.when(productService.updateProduct(nonExistentId, updateDTO))
                .thenThrow(new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND, "Product not found"));

        mockMvc.perform(MockMvcRequestBuilders.put("/products/{id}", nonExistentId)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateDTO)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void updateProductUnauthenticated() throws Exception {
        ProductUpdateDTO updateDTO = new ProductUpdateDTO(
                "Updated Name",
                null,
                null,
                null,
                null
        );

        mockMvc.perform(MockMvcRequestBuilders.put("/products/{id}", testProductId)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateDTO)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void deleteProduct() throws Exception {
        Mockito.doNothing().when(productService).softDeleteProduct(testProductId);

        mockMvc.perform(MockMvcRequestBuilders.delete("/products/{id}", testProductId)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        Mockito.verify(productService).softDeleteProduct(testProductId);
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void deleteProductNotFound() throws Exception {
        Long nonExistentId = 999L;

        Mockito.doThrow(new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.NOT_FOUND, "Product not found"))
                .when(productService).softDeleteProduct(nonExistentId);

        mockMvc.perform(MockMvcRequestBuilders.delete("/products/{id}", nonExistentId)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void deleteProductUnauthenticated() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/products/{id}", testProductId)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void getAllProductsWithAdminFields() throws Exception {
        int page = 0;
        int limit = 12;
        PageRequest pageable = PageRequest.of(page, limit);
        Page<Product> productPage = new PageImpl<>(List.of(testProduct), pageable, 1);

        Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ADMIN"));
        ProductDTO adminProductDTO = new ProductDTO(
                testProductId,
                "Test Product",
                "Test Description",
                99.99,
                10,
                0.1,
                4.5,
                new ArrayList<>(),
                false,
                1L,
                LocalDateTime.now(),
                1L,
                LocalDateTime.now()
        );

        Mockito.when(productService.getAllProducts(page, limit, null, null, null, null, null))
                .thenReturn(productPage);
        Mockito.when(productMapper.mapTo(Mockito.any(Product.class), Mockito.eq(authorities)))
                .thenReturn(adminProductDTO);

        mockMvc.perform(MockMvcRequestBuilders.get("/products"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].createdByName").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].createdAt").exists());
    }
}

