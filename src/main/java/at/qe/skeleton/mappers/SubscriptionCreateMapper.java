package at.qe.skeleton.mappers;

import at.qe.skeleton.dtos.SubscriptionCreateDTO;
import at.qe.skeleton.model.Product;
import at.qe.skeleton.model.Subscription;
import at.qe.skeleton.repositories.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Mapper used to initialize new {@link Subscription} entities from {@link SubscriptionCreateDTO}.
 * <p>It resolves the associated product via the repository during the mapping process.
 * Mapping from entity to creation DTO is not supported.</p>
 */

@Component
public class SubscriptionCreateMapper implements DTOMapper<Subscription, SubscriptionCreateDTO> {

    private final ProductRepository productRepository;

    public SubscriptionCreateMapper(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override //Does not make sense with our current feature-set
    public SubscriptionCreateDTO mapTo(Subscription subscription) {
        throw new UnsupportedOperationException("This Action is not supported!");
    }

    @Override
    public Subscription mapFrom(SubscriptionCreateDTO dto) {
        Subscription subscription = new Subscription();

        Optional<Product> optProduct = productRepository.findById(dto.productId());

        if (optProduct.isPresent()) {
            Product product = optProduct.get();
            subscription.setProduct(product);
        } else {
            throw new EntityNotFoundException("Product not found with id: " + dto.productId());
        }

        subscription.setTypes(dto.types());
        subscription.setChannels(dto.channels());

        return subscription;
    }
}
