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
 * Service implementation of the product service interface.
 * <p>
 * Handles core business logic for products, including creation, update,
 * deletion, and filtered retrieval.
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

    /**
     * Retrieves the price of a product.
     *
     * @param productId the id of the product
     * @return the price of the product or an empty {@code Optional}
     */
    @Override
    public Optional<Double> getProductPrice(Long productId) {
        return productRepository.findById(productId)
                .map(Product::getPrice);
    }

    /**
     * Retrieves the discount of a product.
     *
     * @param productId the id of the product
     * @return the discount of the product or an empty {@code Optional}
     */
    @Override
    public Optional<Double> getProductDiscount(Long productId) {
        return productRepository.findById(productId)
                .map(Product::getDiscount);
    }

    /**
     * Checks whether the requested quantity of a product is available in stock.
     *
     * @param productId the id of the product to check
     * @param quantity the required quantity
     * @return {@code true} if sufficient stock is available, {@code false} otherwise
     */
    @Override
    public boolean isProductAvailable(Long productId, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            return false;
        }
        return productRepository.findById(productId)
                .map(product -> product.getStock() >= quantity && !product.getDeleted())
                .orElse(false);
    }

    /**
     * Updates the product's average score.
     *
     * @param productId the id of the product to update
     * @param averageScore the new average score
     */
    @Override
    @Transactional
    public void updateProductAverageScore(Long productId, Double averageScore) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, PRODUCT_NOT_FOUND_STRING));
        product.setAvgScore(averageScore);
        productRepository.save(product);
    }

    /**
     * Retrieves a paginated list of all products with optional filtering and sorting.
     *
     * @param page the page index
     * @param limit the maximum number of products per page
     * @param minPrice optional filter by minimum price
     * @param maxPrice optional filter by maximum price
     * @param inStock optional filter if the product is in stock
     * @param minRating optional filter by minimum rating
     * @param sort sort specification
     * @param search search specification
     * @return a page of products matching the given criteria
     */
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

    /**
     * Retrieves a product by id.
     *
     * @param id the id of the product
     * @return the product or an empty {@code Optional}
     */
    @Override
    public Optional<Product> getProductById(Long id) {
        return productRepository.findByIdAndNotDeleted(id);
    }

    /**
     * Creates a new product.
     * <p>
     * If an authenticated user is present, the creator is stored on the product.
     *
     * @param product the new product
     * @return the saved product
     */
    @Override
    @Transactional
    public Product createProduct(Product product) {
        Userx currentUser = authenticatedUserService.getAuthenticatedUser();
        if (currentUser != null) {
            product.setCreateUser(currentUser);
        }

        return productRepository.save(product);
    }

    /**
     * Updates a product.
     *
     * @param id the id of the product to update
     * @param updateDTO the UpdateDTO with the updated fields
     * @return the updated product
     * @throws ResponseStatusException 404 if the product does not exist
     */
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

    /**
     * Deletes a product (soft delete).
     * <p>
     * All subscriptions associated with the product are removed.
     *
     * @param id the id of the product to delete
     */
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
     * Parses sort string into Sort object.
     *
     * @param sort the string to sort by (e.g., "name,asc" or "price,desc")
     * @return parsed Sort object of given string
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
     *
     * @param field the sort field to validate
     * @return {@code Boolean}
     */
    private boolean isValidSortField(String field) {
        return field.matches("^[a-zA-Z]+$") &&
                (field.equals("id") || field.equals("name") || field.equals("price") || field.equals("effectivePrice")||
                        field.equals("stock") || field.equals("discount") || field.equals("avgScore") ||
                        field.equals("createDate") || field.equals("updateDate"));
    }

    /**
     * Adjusts product stock levels based on the given product–quantity map.
     *
     * @param items map of product IDs to required quantities
     * @throws ResponseStatusException
     *         404 if a product does not exist
     *         400 if insufficient stock is available
     */
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
