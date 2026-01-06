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
        testUser.setEnabled(true);
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
}