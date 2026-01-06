package at.qe.skeleton.services;

import at.qe.skeleton.dtos.SubscriptionCreateDTO;
import at.qe.skeleton.dtos.SubscriptionUpdateDTO;
import at.qe.skeleton.mappers.SubscriptionCreateMapper;
import at.qe.skeleton.model.Product;
import at.qe.skeleton.model.Subscription;
import at.qe.skeleton.model.Userx;
import at.qe.skeleton.repositories.SubscriptionRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionCreateMapper subscriptionCreateMapper;

    public SubscriptionService(SubscriptionRepository subscriptionRepository,SubscriptionCreateMapper subscriptionCreateMapper) {
        this.subscriptionRepository = subscriptionRepository;
        this.subscriptionCreateMapper = subscriptionCreateMapper;
    }

    public Optional<Subscription> loadSubscription(Long id) {
        return subscriptionRepository.findById(id);
    }

    public Subscription[] loadUserSubscriptions(Userx user) {
        return subscriptionRepository.findByUser(user).toArray(Subscription[]::new);
    }

    public Subscription[] loadProductSubscriptions(Product product){
        return subscriptionRepository.findByProduct(product).toArray(Subscription[]::new);
    }

    public Subscription createSubscription(Userx user, SubscriptionCreateDTO createDTO) {
        Subscription subscription = subscriptionCreateMapper.mapFrom(createDTO);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User must be logged in to create a subscription");
        }
        subscription.setUser(user);
        subscriptionRepository.save(subscription);
        return subscription;
    }

    @Transactional
    public Subscription updateSubscription(Long subscriptionId, SubscriptionUpdateDTO updateDTO) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subscription not found"));

        subscription.setTypes(updateDTO.types());
        subscriptionRepository.save(subscription);
        return subscription;
    }

    @Transactional
    public void deleteSubscription(Long id) {
        Subscription subscription = subscriptionRepository.findById(id).
                orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subscription not found"));
        subscriptionRepository.delete(subscription);
    }
}
