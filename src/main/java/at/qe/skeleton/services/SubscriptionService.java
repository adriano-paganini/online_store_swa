package at.qe.skeleton.services;

import at.qe.skeleton.dtos.SubscriptionUpdateDTO;
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
import at.qe.skeleton.Helpers.SortHelper;

import java.util.List;
import java.util.Optional;


/**
 * Service for managing product subscriptions.
 * <p>
 * This service handles the core business logic for user subscriptions, including
 * creation with conflict checking, filtered retrieval, updates to notification
 * preferences, and subscription deletion.
 */
@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final SortHelper sortHelper;

    public SubscriptionService(SubscriptionRepository subscriptionRepository, SortHelper sortHelper) {
        this.subscriptionRepository = subscriptionRepository;
        this.sortHelper = sortHelper;
    }

    /**
     * Retrieves a specific subscription by user ID and product ID
     *
     * @param userId the id of the user
     * @param productId the id of the product
     * @return the subscription of the user to the product or an empty {@code Optional}
     */
    public Optional<Subscription> getSubscriptionByUserAndProduct(Long userId, Long productId){
        return subscriptionRepository.findByUserAndProduct(userId, productId);
    }

    /**
     * Loads all subscriptions belonging to a specific user as an array
     *
     * @param user the user to load subscriptions from
     * @return the array of subscriptions
     */
    public Subscription[] loadUserSubscriptions(Userx user) {
        return subscriptionRepository.findByUser(user).toArray(Subscription[]::new);
    }

    /**
     * Loads all subscriptions associated with a specific product
     *
     * @param product the product to load connected subscriptions
     * @return an array of connected subscriptions
     */
    public Subscription[] loadProductSubscriptions(Product product) {
        return subscriptionRepository.findByProduct(product).toArray(Subscription[]::new);
    }

    /**
     * Gets paginated subscriptions of a user
     *
     * @param user the user to get subscriptions from
     * @param page the page index
     * @param limit the maximum number of users per page
     * @param types the types of subscriptions
     * @param channels the subscription channels
     * @param sort sort specification
     * @return a page of subscriptions of the user
     */
    public Page<Subscription> getUserSubscriptions(
            Userx user, int page, int limit, SubscriptionType[] types, NotificationType[] channels, String sort) {

        // Utilize SortHelper to validate sort fields, allowing "userId", "types", and "channels", falling back to "product"
        Sort sortObj = sortHelper.parseSort(sort,Subscription.class,
                field -> List.of("userId", "types", "channels").contains(field),
                "product");

        // Create a pageable object with the extracted sort and pagination parameters
        Pageable pageable = PageRequest.of(page, limit, sortObj);

        // Query the repository for filtered and paginated user subscriptions
        return subscriptionRepository.findByUserWithFilter(user.getId(), types, channels, pageable);
    }

    /**
     * Creates a new subscription.
     * <p>
     * Requires an authenticated user.
     * A user may only subscribe once per product.
     *
     * @param user the user who subscribes
     * @param subscription the subscription to create
     * @return the saved subscription
     * @throws ResponseStatusException
     *         401 if the user is not authenticated
     *         409 if the user is already subscribed to the product
     */
    @Transactional
    public Subscription createSubscription(Userx user, Subscription subscription) {
        // Check for existing subscriptions to prevent duplicate product tracking for the same user
        List<Subscription> userSubscriptions = subscriptionRepository.findByUser(user);

        for (Subscription existing : userSubscriptions){
            if (existing.getProduct().equals(subscription.getProduct())){
                throw new ResponseStatusException(HttpStatus.CONFLICT, "User is already subscribed to this product");
            }
        }

        // Ensure a valid user context exists before persistence
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User must be logged in to create a subscription");
        }

        // Assign ownership and persist the new subscription
        subscription.setUser(user);
        subscriptionRepository.save(subscription);
        return subscription;
    }

    /**
     * Updates a subscription.
     *
     * @param subscriptionId the id of the subscription to update
     * @param updateDTO the UpdateDTO with the fields to update
     * @return the updated subscription
     * @throws ResponseStatusException 404 if the subscription does not exist
     */
    @Transactional
    public Subscription updateSubscription(Long subscriptionId, SubscriptionUpdateDTO updateDTO) {
        // Retrieve the existing subscription or throw 404 if not found
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subscription not found"));

        // Update subscription preferences if new values are provided in the DTO
        if (updateDTO.types() != null) {
            subscription.setTypes(updateDTO.types());
        }
        if (updateDTO.channels() != null) {
            subscription.setChannels(updateDTO.channels());
        }

        return subscriptionRepository.save(subscription);
    }

    /**
     * Deletes a subscription.
     *
     * @param id the id of the subscription to delete
     * @throws ResponseStatusException 404 if the subscription does not exist
     */
    @Transactional
    public void deleteSubscription(Long id) {
        // Verify existence before attempting deletion
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subscription not found"));
        // Delete
        subscriptionRepository.delete(subscription);
    }
}