package at.qe.skeleton.tests;

import at.qe.skeleton.dtos.SubscriptionDTO;
import at.qe.skeleton.mappers.SubscriptionMapper;
import at.qe.skeleton.model.*;
import at.qe.skeleton.repositories.ProductRepository;
import at.qe.skeleton.repositories.SubscriptionRepository;
import at.qe.skeleton.repositories.UserxRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SubscriptionMapperTest {

    @Autowired
    private SubscriptionMapper subscriptionMapper;

    @MockitoBean
    private SubscriptionRepository subscriptionRepository;

    @MockitoBean
    private ProductRepository productRepository;

    @MockitoBean
    private UserxRepository userxRepository;

    private Subscription subscription;
    private Userx user;
    private Product product;

    @BeforeEach
    void setUp() {
        user = new Userx();
        user.setId(123L);

        product = new Product();
        product.setId(1L);

        subscription = new Subscription();
        subscription.setId(100L);
        subscription.setUser(user);
        subscription.setProduct(product);
        subscription.setTypes(Set.of(SubscriptionType.RESTOCK));
        subscription.setChannels(Set.of(NotificationType.EMAIL));
    }

    @Test
    void mapTo_ShouldReturnCorrectDto_WhenSubscriptionIsValid() {
        SubscriptionDTO result = subscriptionMapper.mapTo(subscription);

        assertNotNull(result);
        assertEquals(subscription.getId(), result.id());
        assertEquals(user.getId(), result.userId());
        assertEquals(product.getId(), result.productId());
        assertEquals(subscription.getTypes(), result.types());
        assertEquals(subscription.getChannels(), result.channels());
    }

    @Test
    void mapTo_ShouldReturnNull_WhenInputIsNull() {
        assertNull(subscriptionMapper.mapTo(null));
    }

    @Test
    void mapFrom_ShouldReturnNewSubscription_WhenIdIsNull() {
        SubscriptionDTO dto = new SubscriptionDTO(null, 123L, 1L, Set.of(SubscriptionType.RESTOCK), Set.of(NotificationType.EMAIL));

        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        Mockito.when(userxRepository.findById(123L)).thenReturn(Optional.of(user));

        Subscription result = subscriptionMapper.mapFrom(dto);

        assertNotNull(result);
        assertNull(result.getId());
        assertEquals(product, result.getProduct());
        assertEquals(user, result.getUser());
        Mockito.verify(subscriptionRepository, Mockito.never()).findById(Mockito.any());
    }

    @Test
    void mapFrom_ShouldUpdateExistingSubscription_WhenIdExists() {
        SubscriptionDTO dto = new SubscriptionDTO(100L, 123L, 1L, Set.of(SubscriptionType.RESTOCK), Set.of(NotificationType.EMAIL));

        Mockito.when(subscriptionRepository.findById(100L)).thenReturn(Optional.of(subscription));
        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        Mockito.when(userxRepository.findById(123L)).thenReturn(Optional.of(user));

        Subscription result = subscriptionMapper.mapFrom(dto);

        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals(Set.of(SubscriptionType.RESTOCK), result.getTypes());
        Mockito.verify(subscriptionRepository).findById(100L);
    }

    @Test
    void mapFrom_ShouldThrowException_WhenProductNotFound() {
        SubscriptionDTO dto = new SubscriptionDTO(1L, 100L, 999L, null, null);
        Mockito.when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> subscriptionMapper.mapFrom(dto));
    }

    @Test
    void mapFrom_ShouldReturnNull_WhenDtoIsNull() {
        assertNull(subscriptionMapper.mapFrom(null));
    }
}