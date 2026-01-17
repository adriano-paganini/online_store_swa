package at.qe.skeleton.tests;

import at.qe.skeleton.dtos.ProductCreateDTO;
import at.qe.skeleton.mappers.ProductCreateMapper;
import at.qe.skeleton.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ProductCreateMapperTest {

    private ProductCreateMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ProductCreateMapper();
    }

    @Test
    void mapFromWithCompleteDTO() {
        ProductCreateDTO dto = new ProductCreateDTO(
                "Test Product",
                List.of("image1.jpg", "image2.jpg"),
                "Test Description",
                99.99,
                10,
                0.1
        );

        Product product = mapper.mapFrom(dto);

        assertNotNull(product);
        assertEquals("Test Product", product.getName());
        assertEquals("Test Description", product.getDescription());
        assertEquals(99.99, product.getPrice());
        assertEquals(10, product.getStock());
        assertEquals(0.1, product.getDiscount());
        assertEquals(0.0, product.getAvgScore());
        assertFalse(product.getDeleted());
        assertNotNull(product.getImages());
        assertEquals(2, product.getImages().size());
    }

    @Test
    void mapFromWithNullDTO() {
        Product product = mapper.mapFrom(null);

        assertNull(product);
    }

    @Test
    void mapFromWithNullDiscount() {
        ProductCreateDTO dto = new ProductCreateDTO(
                "Test Product",
                null,
                "Test Description",
                99.99,
                10,
                null
        );

        Product product = mapper.mapFrom(dto);

        assertNotNull(product);
        assertEquals(0.0, product.getDiscount(), "Null discount should default to 0.0");
    }

    @Test
    void mapFromWithZeroDiscount() {
        ProductCreateDTO dto = new ProductCreateDTO(
                "Test Product",
                null,
                "Test Description",
                99.99,
                10,
                0.0
        );

        Product product = mapper.mapFrom(dto);

        assertNotNull(product);
        assertEquals(0.0, product.getDiscount());
    }

    @Test
    void mapFromWithNullImages() {
        ProductCreateDTO dto = new ProductCreateDTO(
                "Test Product",
                null,
                "Test Description",
                99.99,
                10,
                0.1
        );

        Product product = mapper.mapFrom(dto);

        assertNotNull(product);
        assertNotNull(product.getImages());
        assertTrue(product.getImages().isEmpty(), "Null images should default to empty list");
    }

    @Test
    void mapFromWithEmptyImages() {
        ProductCreateDTO dto = new ProductCreateDTO(
                "Test Product",
                new ArrayList<>(),
                "Test Description",
                99.99,
                10,
                0.1
        );

        Product product = mapper.mapFrom(dto);

        assertNotNull(product);
        assertNotNull(product.getImages());
        assertTrue(product.getImages().isEmpty());
    }

    @Test
    void mapFromSetsDefaultValues() {
        ProductCreateDTO dto = new ProductCreateDTO(
                "Test Product",
                null,
                "Test Description",
                99.99,
                10,
                null
        );

        Product product = mapper.mapFrom(dto);

        assertNotNull(product);
        assertEquals(0.0, product.getAvgScore(), "AvgScore should default to 0.0");
        assertFalse(product.getDeleted(), "Deleted should default to false");
    }

    @Test
    void mapFromWithHighDiscount() {
        ProductCreateDTO dto = new ProductCreateDTO(
                "Test Product",
                null,
                "Test Description",
                100.0,
                10,
                0.9
        );

        Product product = mapper.mapFrom(dto);

        assertNotNull(product);
        assertEquals(0.9, product.getDiscount());
    }

    @Test
    void mapFromWithZeroStock() {
        ProductCreateDTO dto = new ProductCreateDTO(
                "Test Product",
                null,
                "Test Description",
                99.99,
                0,
                0.1
        );

        Product product = mapper.mapFrom(dto);

        assertNotNull(product);
        assertEquals(0, product.getStock());
    }

    @Test
    void mapFromWithNegativePrice() {
        ProductCreateDTO dto = new ProductCreateDTO(
                "Test Product",
                null,
                "Test Description",
                -10.0,
                10,
                0.1
        );

        Product product = mapper.mapFrom(dto);

        assertNotNull(product);
        assertEquals(-10.0, product.getPrice(), "Mapper should preserve negative price (validation elsewhere)");
    }
}
