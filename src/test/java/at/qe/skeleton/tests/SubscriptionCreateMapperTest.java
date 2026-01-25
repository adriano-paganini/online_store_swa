package at.qe.skeleton.tests;

import at.qe.skeleton.dtos.SubscriptionCreateDTO;
import at.qe.skeleton.mappers.SubscriptionCreateMapper;
import at.qe.skeleton.model.NotificationType;
import at.qe.skeleton.model.Product;
import at.qe.skeleton.model.Subscription;
import at.qe.skeleton.model.SubscriptionType;
import at.qe.skeleton.repositories.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SubscriptionCreateMapperTest {

    @Autowired
    private SubscriptionCreateMapper mapper;

    @MockitoBean
    private ProductRepository productRepository;

    @Test
    void mapFrom_ShouldReturnSubscription_WhenProductExists() {
        Long productId = 10L;
        Product mockProduct = new Product();
        mockProduct.setId(productId);

        SubscriptionCreateDTO dto = new SubscriptionCreateDTO(
                productId,
                Set.of(SubscriptionType.RESTOCK),
                Set.of(NotificationType.EMAIL)
        );

        Mockito.when(productRepository.findById(productId)).thenReturn(Optional.of(mockProduct));

        Subscription result = mapper.mapFrom(dto);

        assertNotNull(result);
        assertEquals(mockProduct, result.getProduct());
        assertEquals(dto.types(), result.getTypes());
        assertEquals(dto.channels(), result.getChannels());
        Mockito.verify(productRepository).findById(productId);
    }

    @Test
    void mapFrom_ShouldThrowEntityNotFoundException_WhenProductDoesNotExist() {
        Long productId = 999L;
        SubscriptionCreateDTO dto = new SubscriptionCreateDTO(productId, Set.of(), Set.of());

        Mockito.when(productRepository.findById(productId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                mapper.mapFrom(dto)
        );

        assertTrue(exception.getMessage().contains("Product not found with id: " + productId));
    }

    @Test
    void mapTo_ShouldThrowUnsupportedOperationException() {
        Subscription subscription = new Subscription();

        assertThrows(UnsupportedOperationException.class, () ->
                mapper.mapTo(subscription)
        );
    }
}