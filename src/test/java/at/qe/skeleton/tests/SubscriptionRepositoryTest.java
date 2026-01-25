package at.qe.skeleton.tests;

import at.qe.skeleton.model.*;
import at.qe.skeleton.repositories.ProductRepository;
import at.qe.skeleton.repositories.SubscriptionRepository;
import at.qe.skeleton.repositories.UserxRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Set;

@DataJpaTest
public class SubscriptionRepositoryTest {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private UserxRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    private Userx testUser;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        testUser = new Userx();
        testUser.setUsername("repoUser");
        testUser.setPassword("password");
        testUser = userRepository.save(testUser);

        testProduct = new Product();
        testProduct.setName("Test Tree");
        testProduct.setDescription("Much needed Description. Informative");
        testProduct.setStock(5);
        testProduct.setAvgScore(0.0);
        testProduct.setDeleted(false);
        testProduct.setPrice(9001.00);

        testProduct = productRepository.save(testProduct);
    }

    @Test
    void testFindByProductAndType() {
        Subscription sub = new Subscription();
        sub.setUser(testUser);
        sub.setProduct(testProduct);
        sub.setTypes(Set.of(SubscriptionType.PRICEUPDATE, SubscriptionType.RESTOCK));
        subscriptionRepository.save(sub);

        List<Subscription> found = subscriptionRepository.findByProductAndType(
                testProduct.getId(),
                SubscriptionType.PRICEUPDATE
        );

        Assertions.assertEquals(1, found.size());
        Assertions.assertTrue(found.getFirst().getTypes().contains(SubscriptionType.PRICEUPDATE));

        List<Subscription> notFound = subscriptionRepository.findByProductAndType(
                testProduct.getId(),
                SubscriptionType.NAMEUPDATE
        );
        Assertions.assertTrue(notFound.isEmpty());
    }

    @Test
    void testFindByUser() {
        Subscription sub = new Subscription();
        sub.setUser(testUser);
        sub.setProduct(testProduct);
        sub.setTypes(Set.of(SubscriptionType.RESTOCK));
        subscriptionRepository.save(sub);

        List<Subscription> results = subscriptionRepository.findByUser(testUser);

        Assertions.assertFalse(results.isEmpty());
        Assertions.assertEquals(testUser.getId(), results.getFirst().getUser().getId());
    }
    @Test
    void testFindByUserWithFilterMatchesIntersection() {
        Subscription sub = new Subscription();
        sub.setUser(testUser);
        sub.setProduct(testProduct);
        sub.setTypes(Set.of(SubscriptionType.PRICEUPDATE, SubscriptionType.RESTOCK));
        sub.setChannels(Set.of(NotificationType.EMAIL, NotificationType.SMS));
        subscriptionRepository.save(sub);

        SubscriptionType[] filterTypes = {SubscriptionType.PRICEUPDATE};
        NotificationType[] filterChannels = {NotificationType.EMAIL};

        org.springframework.data.domain.Page<Subscription> page = subscriptionRepository.findByUserWithFilter(
                testUser.getId(), filterTypes, filterChannels, org.springframework.data.domain.PageRequest.of(0, 10));

        Assertions.assertEquals(1, page.getTotalElements());
        Assertions.assertTrue(page.getContent().getFirst().getTypes().contains(SubscriptionType.PRICEUPDATE));
    }

    @Test
    void testFindByUserWithFilterNoMatch() {
        Subscription sub = new Subscription();
        sub.setUser(testUser);
        sub.setProduct(testProduct);
        sub.setTypes(Set.of(SubscriptionType.RESTOCK));
        subscriptionRepository.save(sub);

        SubscriptionType[] filterTypes = {SubscriptionType.PRICEUPDATE};

        org.springframework.data.domain.Page<Subscription> page = subscriptionRepository.findByUserWithFilter(
                testUser.getId(), filterTypes, null, org.springframework.data.domain.PageRequest.of(0, 10));

        Assertions.assertEquals(0, page.getTotalElements());
    }

    @Test
    void testFindByUserWithFilterNullFiltersReturnsAll() {
        Subscription sub1 = new Subscription();
        sub1.setUser(testUser);
        sub1.setProduct(testProduct);
        sub1.setTypes(Set.of(SubscriptionType.PRICEUPDATE));
        subscriptionRepository.save(sub1);

        Product secondProduct = new Product();
        secondProduct.setName("Second Tree");
        secondProduct.setDescription("Another informative description");
        secondProduct.setStock(10);
        secondProduct.setAvgScore(0.0);
        secondProduct.setDeleted(false);
        secondProduct.setPrice(150.00);
        secondProduct = productRepository.save(secondProduct);

        Subscription sub2 = new Subscription();
        sub2.setUser(testUser);
        sub2.setProduct(secondProduct);
        sub2.setTypes(Set.of(SubscriptionType.RESTOCK));
        subscriptionRepository.save(sub2);

        org.springframework.data.domain.Page<Subscription> page = subscriptionRepository.findByUserWithFilter(
                testUser.getId(), null, null, org.springframework.data.domain.PageRequest.of(0, 10));

        Assertions.assertEquals(2, page.getTotalElements());
    }

    @Test
    void testFindByUserWithFilterDistinctResults() {
        Subscription sub = new Subscription();
        sub.setUser(testUser);
        sub.setProduct(testProduct);
        sub.setTypes(Set.of(SubscriptionType.PRICEUPDATE, SubscriptionType.RESTOCK));
        subscriptionRepository.save(sub);

        SubscriptionType[] filterTypes = {SubscriptionType.PRICEUPDATE, SubscriptionType.RESTOCK};

        org.springframework.data.domain.Page<Subscription> page = subscriptionRepository.findByUserWithFilter(
                testUser.getId(), filterTypes, null, org.springframework.data.domain.PageRequest.of(0, 10));

        Assertions.assertEquals(1, page.getTotalElements());
    }
}