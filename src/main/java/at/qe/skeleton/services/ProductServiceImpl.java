package at.qe.skeleton.services;

import at.qe.skeleton.dtos.ProductUpdateDTO;
import at.qe.skeleton.events.*;
import at.qe.skeleton.mappers.ProductMapper;
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

import java.util.Map;
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
    private final ProductMapper productMapper;
    private final String PRODUCT_NOT_FOUND_STRING = "Product not found";

    public ProductServiceImpl(
            ProductRepository productRepository,
            AuthenticatedUserService authenticatedUserService,
            ApplicationEventPublisher applicationEventPublisher,
            SubscriptionService subscriptionService,
            ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.authenticatedUserService = authenticatedUserService;
        this.applicationEventPublisher = applicationEventPublisher;
        this.subscriptionService = subscriptionService;
        this.productMapper = productMapper;
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
        if (quantity == null || quantity <= 0) {
            return false;
        }
        return productRepository.findById(productId)
                .map(product -> product.getStock() >= quantity && !product.getDeleted())
                .orElse(false);
    }

    @Override
    @Transactional
    public void updateProductAverageScore(Long productId, Double averageScore) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, PRODUCT_NOT_FOUND_STRING));
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
            String sort,
            String search) {

        Sort sortObj = parseSort(sort);
        Pageable pageable = PageRequest.of(page, limit, sortObj);

        return productRepository.findAllWithFilters(
                minPrice, maxPrice, inStock, minRating, search,pageable);
    }

    @Override
    public Optional<Product> getProductById(Long id) {
        return productRepository.findByIdAndNotDeleted(id);
    }

    @Override
    @Transactional
    public Product createProduct(Product product) {
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
                        HttpStatus.NOT_FOUND, PRODUCT_NOT_FOUND_STRING));
        
        // Store old values for event publishing (service can access DTO to keep controller thin)
        String oldName = product.getName();
        String oldDescription = product.getDescription();
        Double oldPrice = product.getPrice();
        Integer oldStock = product.getStock();
        Double oldDiscount = product.getDiscount();
        
        // Apply DTO changes via mapper
        productMapper.apply(product, updateDTO);
        

        if (updateDTO.name() != null && !oldName.equals(updateDTO.name())) {
            applicationEventPublisher.publishEvent(new ProductNameUpdateEvent(
                    product, oldName, updateDTO.name()));
        }
        if (updateDTO.description() != null && !oldDescription.equals(updateDTO.description())) {
            applicationEventPublisher.publishEvent(new ProductDescriptionUpdateEvent(
                    product, oldDescription, updateDTO.description()));
        }
        if (updateDTO.price() != null && !oldPrice.equals(updateDTO.price())) {
            applicationEventPublisher.publishEvent(new ProductPriceUpdateEvent(
                    product, oldPrice, updateDTO.price()));
        }
        if (updateDTO.stock() != null && oldStock < updateDTO.stock()) {
            applicationEventPublisher.publishEvent(new ProductRestockEvent(product,
                    oldStock, updateDTO.stock()));
        }
        if (updateDTO.discount() != null) {
            Double newDiscount = updateDTO.discount();
            if (!newDiscount.equals(oldDiscount) && newDiscount > 0) {
                applicationEventPublisher.publishEvent(new ProductDiscountUpdateEvent(
                        product, oldDiscount, newDiscount));
            }
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
                        HttpStatus.NOT_FOUND, PRODUCT_NOT_FOUND_STRING));

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


        if (field.equals("price")){
            field = "effectivePrice";
        }

        return Sort.by(sortDirection, field);
    }

    /**
     * Validates that the sort field is a valid Product entity field.
     */
    private boolean isValidSortField(String field) {
        return field.matches("^[a-zA-Z]+$") &&
                (field.equals("id") || field.equals("name") || field.equals("price") || field.equals("effectivePrice")||
                        field.equals("stock") || field.equals("discount") || field.equals("avgScore") ||
                        field.equals("createDate") || field.equals("updateDate"));
    }


    @Override
    @Transactional
    public void adjustProductStockWithMap(Map<Long,Integer> items) {
        for (Long productId:items.keySet()) {
            Product product = getProductById(productId).orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, PRODUCT_NOT_FOUND_STRING));

            Integer stock = product.getStock();
            Integer requiredQuantity = items.get(productId);
            if (stock >= requiredQuantity) {
                product.setStock(stock - requiredQuantity);
            }else if (requiredQuantity>0){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Not enough Stock");
            }
        }
    }
}
