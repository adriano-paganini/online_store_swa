package at.qe.skeleton.services;

import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class ProductServiceImpl implements ProductService {

    @Override
    public Optional<Double> getProductPrice(Long productId) {
        // TODO: Implement when Product entity is created
        // For now, return a default price
        return Optional.of(0.0);
    }

    @Override
    public Optional<Double> getProductDiscount(Long productId) {
        // TODO: Implement when Product entity is created
        // For now, return no discount
        return Optional.of(0.0);
    }

    @Override
    public boolean isProductAvailable(Long productId, Integer quantity) {
        // TODO: Implement when Product entity is created
        // For now, assume all products are available
        return true;
    }
}

