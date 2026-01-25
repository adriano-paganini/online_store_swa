package at.qe.skeleton.mappers;

import at.qe.skeleton.dtos.SubscriptionDTO;
import at.qe.skeleton.model.Subscription;
import at.qe.skeleton.repositories.ProductRepository;
import at.qe.skeleton.repositories.SubscriptionRepository;
import at.qe.skeleton.repositories.UserxRepository;

import org.springframework.stereotype.Service;

/**
 * Standard mapper for bidirectional conversion between {@link Subscription} entities and {@link SubscriptionDTO}.
 * <p>Handles full entity resolution by looking up existing subscriptions, products,
 * and users from their respective repositories.</p>
 */

@Service
public class SubscriptionMapper implements DTOMapper<Subscription, SubscriptionDTO> {

    private final SubscriptionRepository subscriptionRepository;
    private final ProductRepository productRepository;
    private final UserxRepository userxRepository;

    public SubscriptionMapper(SubscriptionRepository subscriptionRepository,
                              ProductRepository productRepository,
                              UserxRepository userxRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.productRepository = productRepository;
        this.userxRepository = userxRepository;
    }

    @Override
    public SubscriptionDTO mapTo(Subscription subscription) {
        if (subscription == null) return null;

        return new SubscriptionDTO(
                subscription.getId(),
                subscription.getUser().getId(),
                subscription.getProduct().getId(),
                subscription.getTypes(),
                subscription.getChannels());
    }

    @Override
    public Subscription mapFrom(SubscriptionDTO dto) {
        if (dto == null) return null;

        Subscription subscription = (dto.id() != null)
                ? subscriptionRepository.findById(dto.id()).orElseGet(Subscription::new)
                : new Subscription();

        subscription.setProduct(productRepository.findById(dto.productId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + dto.productId())));

        subscription.setUser(userxRepository.findById(dto.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + dto.userId())));

        subscription.setTypes(dto.types());
        subscription.setChannels(dto.channels());

        return subscription;
    }
}
