package at.qe.skeleton.tests;

import at.qe.skeleton.dtos.SubscriptionUpdateDTO;
import at.qe.skeleton.mappers.SubscriptionCreateMapper;
import at.qe.skeleton.model.*;
import at.qe.skeleton.repositories.SubscriptionRepository;
import at.qe.skeleton.services.SubscriptionService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class SubscriptionServiceTest {

    @Autowired
    private SubscriptionService subscriptionService;

    @MockitoBean
    private SubscriptionRepository subscriptionRepository;

    @MockitoBean
    private SubscriptionCreateMapper subscriptionCreateMapper;

    private Userx testUser;
    private Subscription testSubscription;

    @BeforeEach
    void setUp() {
        testUser = new Userx();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testSubscription = new Subscription();
        testSubscription.setId(100L);
        testSubscription.setUser(testUser);
        testSubscription.setTypes(Set.of(SubscriptionType.PRICEUPDATE));
    }

    @Test
    void testCreateSubscriptionLogicAndPersistence() {
        Subscription result = subscriptionService.createSubscription(testUser, testSubscription);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(testUser, result.getUser());

        ArgumentCaptor<Subscription> subCaptor = ArgumentCaptor.forClass(Subscription.class);
        verify(subscriptionRepository).save(subCaptor.capture());
        Assertions.assertEquals(testUser, subCaptor.getValue().getUser());
    }

    @Test
    void testCreateDuplicateSubscription(){
        Product product = new Product();
        product.setId(50L);

        Subscription existing = new Subscription();
        existing.setId(200L);
        existing.setUser(testUser);
        existing.setProduct(product);

        testSubscription.setProduct(product);

        when(subscriptionRepository.findByUser(testUser)).thenReturn(List.of(existing));

        ResponseStatusException ex = Assertions.assertThrows(ResponseStatusException.class,
                () -> subscriptionService.createSubscription(testUser, testSubscription));

        Assertions.assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        Assertions.assertNotNull(ex.getReason());
        Assertions.assertTrue(ex.getReason().contains("Already Subscribed"));
    }

    @Test
    void testLoadSubscriptionById(){
        when(subscriptionRepository.findById(100L)).thenReturn(Optional.of(testSubscription));

        Optional<Subscription> result = subscriptionService.loadSubscription(100L);

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(testSubscription, result.get());
        verify(subscriptionRepository).findById(100L);
    }

    @Test
    void testGetSubscriptionByUserAndProduct(){
        Long userId = 1L;
        Long productId = 50L;

        when(subscriptionRepository.findByUserAndProduct(userId, productId))
                .thenReturn(Optional.of(testSubscription));

        Optional<Subscription> result = subscriptionService.getSubscriptionByUserAndProduct(userId, productId);

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(testSubscription, result.get());
        verify(subscriptionRepository).findByUserAndProduct(userId, productId);
    }


    @Test
    void testCreateSubscriptionThrowsUnauthorizedIfUserNull() {
        ResponseStatusException ex = Assertions.assertThrows(ResponseStatusException.class,
                () -> subscriptionService.createSubscription(null, testSubscription));

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
    }

    @Test
    void testUpdateSubscriptionLogic() {
        Set<SubscriptionType> newTypes = Set.of(SubscriptionType.DISCOUNTUPDATE, SubscriptionType.RESTOCK);
        Set<NotificationType> newChannels = Set.of(NotificationType.SMS);
        SubscriptionUpdateDTO updateDTO = new SubscriptionUpdateDTO(newTypes, newChannels);

        when(subscriptionRepository.findById(100L)).thenReturn(Optional.of(testSubscription));

        when(subscriptionRepository.save(org.mockito.ArgumentMatchers.any(Subscription.class)))
                .thenReturn(testSubscription);

        Subscription updated = subscriptionService.updateSubscription(100L, updateDTO);

        Assertions.assertNotNull(updated);
        Assertions.assertEquals(newTypes, updated.getTypes());
        Assertions.assertEquals(newChannels, updated.getChannels()) ;
        verify(subscriptionRepository).save(testSubscription);
    }

    @Test
    void testDeleteSubscriptionSuccess() {
        when(subscriptionRepository.findById(100L)).thenReturn(Optional.of(testSubscription));

        subscriptionService.deleteSubscription(100L);

        verify(subscriptionRepository).delete(testSubscription);
    }

    @Test
    void testLoadMethods() {
        when(subscriptionRepository.findByUser(testUser)).thenReturn(List.of(testSubscription));
        Subscription[] results = subscriptionService.loadUserSubscriptions(testUser);

        Assertions.assertEquals(1, results.length);
        Assertions.assertEquals(testSubscription, results[0]);
    }
    @Test
    void testGetUserSubscriptionsPaginationAndFiltering() {
        int page = 0;
        int limit = 10;
        SubscriptionType[] types = {SubscriptionType.PRICEUPDATE};
        NotificationType[] channels = {NotificationType.EMAIL};
        String sort = "productId,asc";

        Page<Subscription> expectedPage = org.springframework.data.domain.Page.empty();

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        when(subscriptionRepository.findByUserWithFilter(
                org.mockito.ArgumentMatchers.eq(testUser.getId()),
                org.mockito.ArgumentMatchers.eq(types),
                org.mockito.ArgumentMatchers.eq(channels),
                pageableCaptor.capture()
        )).thenReturn(expectedPage);

        Page<Subscription> result = subscriptionService.getUserSubscriptions(
                testUser, page, limit, types, channels, sort);

        Assertions.assertNotNull(result);
        Pageable capturedPageable = pageableCaptor.getValue();
        Assertions.assertEquals(page, capturedPageable.getPageNumber());
        Assertions.assertEquals(limit, capturedPageable.getPageSize());
        Assertions.assertTrue(Objects.requireNonNull(capturedPageable.getSort().getOrderFor("product")).isAscending());

        verify(subscriptionRepository).findByUserWithFilter(testUser.getId(), types, channels, capturedPageable);
    }

    @Test
    void testLoadProductSubscriptions() {
        Product testProduct = new Product();
        testProduct.setId(50L);
        when(subscriptionRepository.findByProduct(testProduct)).thenReturn(List.of(testSubscription));

        Subscription[] results = subscriptionService.loadProductSubscriptions(testProduct);

        Assertions.assertEquals(1, results.length);
        Assertions.assertEquals(testSubscription, results[0]);
        verify(subscriptionRepository).findByProduct(testProduct);
    }

    @Test
    void testUpdateSubscriptionThrowsNotFound() {
        Long invalidId = 999L;
        SubscriptionUpdateDTO updateDTO = new SubscriptionUpdateDTO(Set.of(), Set.of());
        when(subscriptionRepository.findById(invalidId)).thenReturn(Optional.empty());

        ResponseStatusException ex = Assertions.assertThrows(ResponseStatusException.class,
                () -> subscriptionService.updateSubscription(invalidId, updateDTO));

        Assertions.assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        Assertions.assertNotNull(ex.getReason());
        Assertions.assertTrue(ex.getReason().contains("Subscription not found"));
    }

    @Test
    void testDeleteSubscriptionThrowsNotFound() {
        Long invalidId = 999L;
        when(subscriptionRepository.findById(invalidId)).thenReturn(Optional.empty());

        ResponseStatusException ex = Assertions.assertThrows(ResponseStatusException.class,
                () -> subscriptionService.deleteSubscription(invalidId));

        Assertions.assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }
}