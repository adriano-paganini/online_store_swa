package at.qe.skeleton.services;

import at.qe.skeleton.dtos.ProductCreateDTO;
import at.qe.skeleton.dtos.ProductUpdateDTO;
import at.qe.skeleton.events.*;
import at.qe.skeleton.model.Product;
import at.qe.skeleton.model.Subscription;
import at.qe.skeleton.model.Userx;
import at.qe.skeleton.repositories.ProductRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

/**
 * Service implementation for product operations.
 */
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final AuthenticatedUserService authenticatedUserService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final SubscriptionService subscriptionService;

    public ProductServiceImpl(
            ProductRepository productRepository,
            AuthenticatedUserService authenticatedUserService,
            ApplicationEventPublisher applicationEventPublisher,
            SubscriptionService subscriptionService) {
        this.productRepository = productRepository;
        this.authenticatedUserService = authenticatedUserService;
        this.applicationEventPublisher = applicationEventPublisher;
        this.subscriptionService = subscriptionService;
    }

    @Override
    public Optional<Double> getProductPrice(Long productId) {
        return productRepository.findById(productId)
                .map(Product::getPrice);
    }

    @Override
    public Optional<Double> getProductDiscount(Long productId) {
        return productRepository.findById(productId)
                .map(Product::getDiscount);
    }

    @Override
    public boolean isProductAvailable(Long productId, Integer quantity) {
        return productRepository.findById(productId)
                .map(product -> product.getStock() >= quantity && !product.getDeleted())
                .orElse(false);
    }

    @Override
    @Transactional
    public void updateProductAverageScore(Long productId, Double averageScore) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Product not found"));
        product.setAvgScore(averageScore);
        productRepository.save(product);
    }

    @Override
    public Page<Product> getAllProducts(
            int page,
            int limit,
            Double minPrice,
            Double maxPrice,
            Boolean inStock,
            Double minRating,
            String sort) {

        Sort sortObj = parseSort(sort);
        Pageable pageable = PageRequest.of(page, limit, sortObj);

        return productRepository.findAllWithFilters(
                minPrice, maxPrice, inStock, minRating, pageable);
    }

    @Override
    public Optional<Product> getProductById(Long id) {
        return productRepository.findByIdAndNotDeleted(id);
    }

    @Override
    @Transactional
    public Product createProduct(ProductCreateDTO createDTO) {
        Product product = new Product();
        product.setName(createDTO.name());
        product.setDescription(createDTO.description());
        product.setPrice(createDTO.price());
        product.setStock(createDTO.stock());
        product.setDiscount(createDTO.discount() != null ? createDTO.discount() : 0.0);
        product.setAvgScore(0.0);
        product.setDeleted(false);
        product.setImages(new java.util.ArrayList<>());

        Userx currentUser = authenticatedUserService.getAuthenticatedUser();
        if (currentUser != null) {
            product.setCreateUser(currentUser);
        }

        return productRepository.save(product);
    }

    @Override
    @Transactional
    public Product updateProduct(Long id, ProductUpdateDTO updateDTO) {
        Product product = productRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Product not found"));
        //TODO:ADD MORE EVENT TYPES
        if (updateDTO.name() != null) {
            applicationEventPublisher.publishEvent(new ProductNameUpdateEvent(
                    product, product.getName(), updateDTO.name()));
            product.setName(updateDTO.name());
        }
        if (updateDTO.description() != null) {
            applicationEventPublisher.publishEvent(new ProductDescriptionUpdateEvent(
                    product, product.getDescription(), updateDTO.description()));
            product.setDescription(updateDTO.description());
        }
        if (updateDTO.price() != null) {
            applicationEventPublisher.publishEvent(new ProductPriceUpdateEvent(
                    product, product.getPrice(), updateDTO.price()));
            product.setPrice(updateDTO.price());
        }
        if (updateDTO.stock() != null) {
            Integer oldValue = product.getStock();
            Integer newValue = updateDTO.stock();
            if (oldValue < newValue) {
                applicationEventPublisher.publishEvent(new ProductRestockEvent(product, oldValue, newValue));
            }
            product.setStock(updateDTO.stock());
        }
        if (updateDTO.discount() != null) {
            applicationEventPublisher.publishEvent(new ProductDiscountUpdateEvent(
                    product, product.getDiscount(), updateDTO.discount()));
            product.setDiscount(updateDTO.discount());
        }

        Userx currentUser = authenticatedUserService.getAuthenticatedUser();
        if (currentUser != null) {
            product.setUpdateUser(currentUser);
        }

        return productRepository.save(product);
    }

    @Override
    @Transactional
    public void softDeleteProduct(Long id) {
        Product product = productRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Product not found"));

        product.setDeleted(true);
        productRepository.save(product);

        Subscription[] subscriptions = subscriptionService.loadProductSubscriptions(product);
        for (Subscription s : subscriptions){
            subscriptionService.deleteSubscription(s.getId());
        }
    }

    /**
     * Parses sort string (e.g., "name,asc" or "price,desc") into Sort object.
     */
    private Sort parseSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return Sort.by(Sort.Direction.ASC, "id");
        }

        String[] parts = sort.split(",");
        if (parts.length != 2) {
            return Sort.by(Sort.Direction.ASC, "id");
        }

        String field = parts[0].trim();
        String direction = parts[1].trim().toLowerCase();

        Sort.Direction sortDirection = "desc".equals(direction)
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        // Validate field name to prevent SQL injection
        if (!isValidSortField(field)) {
            return Sort.by(Sort.Direction.ASC, "id");
        }

        return Sort.by(sortDirection, field);
    }

    /**
     * Validates that the sort field is a valid Product entity field.
     */
    private boolean isValidSortField(String field) {
        return field.matches("^[a-zA-Z]+$") &&
                (field.equals("id") || field.equals("name") || field.equals("price") ||
                        field.equals("stock") || field.equals("discount") || field.equals("avgScore") ||
                        field.equals("createDate") || field.equals("updateDate"));
    }
}
