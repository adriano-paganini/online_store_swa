package at.qe.skeleton.services;

import at.qe.skeleton.dtos.SubscriptionCreateDTO;
import at.qe.skeleton.dtos.SubscriptionUpdateDTO;
import at.qe.skeleton.mappers.SubscriptionCreateMapper;
import at.qe.skeleton.model.*;
import at.qe.skeleton.repositories.SubscriptionRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static at.qe.skeleton.Helpers.SortHelper.parseSort;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionService(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    public Optional<Subscription> loadSubscription(Long id) {
        return subscriptionRepository.findById(id);
    }

    public Optional<Subscription> getSubscriptionByUserAndProduct(Long userId, Long productId){
        return subscriptionRepository.findByUserAndProduct(userId,productId);
    }

    public Subscription[] loadUserSubscriptions(Userx user) {
        return subscriptionRepository.findByUser(user).toArray(Subscription[]::new);
    }

    public Subscription[] loadProductSubscriptions(Product product) {
        return subscriptionRepository.findByProduct(product).toArray(Subscription[]::new);
    }

    public Page<Subscription> getUserSubscriptions(
            Userx user, int page, int limit, SubscriptionType[] types, NotificationType[] channels, String sort) {

        Sort sortObj = parseSort(sort,
                field -> List.of("userId","types","channels").contains(field),
                "product");

        Pageable pageable = PageRequest.of(page, limit, sortObj);

        return subscriptionRepository.findByUserWithFilter(user.getId(), types, channels, pageable);
    }


    public Subscription createSubscription(Userx user, Subscription subscription) {
        List<Subscription> userSubscriptions = subscriptionRepository.findByUser(user);

        for (Subscription forSubscription:userSubscriptions){
            if (forSubscription.getProduct().equals(subscription.getProduct())){
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Already Subscribed");
            }
        }

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

        if (updateDTO.types() != null) {
            subscription.setTypes(updateDTO.types());
        }
        if (updateDTO.channels() != null) {
            subscription.setChannels(updateDTO.channels());
        }

        return subscriptionRepository.save(subscription);
    }

    @Transactional
    public void deleteSubscription(Long id) {
        Subscription subscription = subscriptionRepository.findById(id).
                orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subscription not found"));
        subscriptionRepository.delete(subscription);
    }
}
