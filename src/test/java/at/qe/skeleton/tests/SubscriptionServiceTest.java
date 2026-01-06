package at.qe.skeleton.tests;

import at.qe.skeleton.dtos.SubscriptionCreateDTO;
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
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
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
    void testCreateSubscription_LogicAndPersistence() {
        SubscriptionCreateDTO createDTO = new SubscriptionCreateDTO(
                10L,
                Set.of(SubscriptionType.RESTOCK),
                Set.of(NotificationType.EMAIL)
        );

        Subscription mappedSub = new Subscription();
        when(subscriptionCreateMapper.mapFrom(createDTO)).thenReturn(mappedSub);

        Subscription result = subscriptionService.createSubscription(testUser, createDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(testUser, result.getUser());

        ArgumentCaptor<Subscription> subCaptor = ArgumentCaptor.forClass(Subscription.class);
        verify(subscriptionRepository).save(subCaptor.capture());
        Assertions.assertEquals(testUser, subCaptor.getValue().getUser());
    }

    @Test
    void testCreateSubscriptionThrowsUnauthorizedIfUserNull() {
        SubscriptionCreateDTO createDTO = new SubscriptionCreateDTO(10L, Set.of(), Set.of());

        ResponseStatusException ex = Assertions.assertThrows(ResponseStatusException.class,
                () -> subscriptionService.createSubscription(null, createDTO));

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
    }

    @Test
    void testUpdateSubscriptionLogic() {
        Set<SubscriptionType> newTypes = Set.of(SubscriptionType.DISCOUNTUPDATE, SubscriptionType.RESTOCK);
        SubscriptionUpdateDTO updateDTO = new SubscriptionUpdateDTO(newTypes, Set.of(NotificationType.SMS));

        when(subscriptionRepository.findById(100L)).thenReturn(Optional.of(testSubscription));

        Subscription updated = subscriptionService.updateSubscription(100L, updateDTO);

        Assertions.assertEquals(newTypes, updated.getTypes());
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
}