package at.qe.skeleton.services;

import at.qe.skeleton.Helpers.SortHelper;
import at.qe.skeleton.model.Review;
import at.qe.skeleton.model.Userx;
import at.qe.skeleton.repositories.ReviewRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;
import java.util.Optional;

/**
 * Service for managing product reviews.
 * <p>
 * This service handles the core business logic for product reviews, including
 * creation and filtered retrieval.
 */
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final AuthenticatedUserService authenticatedUserService;
    private final ProductService productService;
    private final SortHelper sortHelper;

    public ReviewService(
            ReviewRepository reviewRepository,
            AuthenticatedUserService authenticatedUserService,
            ProductService productService, SortHelper sortHelper) {
        this.reviewRepository = reviewRepository;
        this.authenticatedUserService = authenticatedUserService;
        this.productService = productService;
        this.sortHelper = sortHelper;
    }


    /**
     * Gets a paginated list of reviews of a product with optional filtering and sorting.
     *
     * @param productId the id of the product
     * @param page the page index
     * @param limit the maximum number of reviews per page
     * @param minRating the minimal rating to filter by
     * @param maxRating the maximal rating to filter by
     * @param sort sort specification
     * @return a page of review matching the given criteria
     */
    public Page<Review> getProductReviews(
            Long productId,
            int page,
            int limit,
            Integer minRating,
            Integer maxRating,
            String sort) {


        Sort sortObj = sortHelper.parseSort(sort,Review.class,
                field -> Objects.equals("score", field),
                "timestamp");

        Pageable pageable = PageRequest.of(page, limit, sortObj);

        return reviewRepository.findByProductIdWithFilters(productId, minRating, maxRating, pageable);
    }

    /**
     * Creates a new review for a product.
     * <p>
     * Requires an authenticated user.
     * A user may only create one review per product.
     * The review score must be between 1 and 5.
     *
     * @param productId the id of the product
     * @param review the review to create
     * @return the saved review
     * @throws ResponseStatusException
     *         401 if the user is not authenticated
     *         409 if the user already reviewed the product
     *         400 if the review score is invalid
     */
    @Transactional
    public Review createReview(Long productId, Review review) {
        Userx currentUser = authenticatedUserService.getAuthenticatedUser();
        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User must be authenticated");
        }

        Optional<Review> existingReview = reviewRepository.findByUser_IdAndProductId(
                currentUser.getId(), productId);

        if (existingReview.isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "User already has a review for this product");
        }

        // Validate business rules on entity attributes
        if (review.getScore() == null || review.getScore() < 1 || review.getScore() > 5) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Score must be between 1 and 5");
        }

        // Trim content if present
        if (review.getContent() != null) {
            review.setContent(review.getContent().trim());
        }

        review.setUser(currentUser);
        review.setProductId(productId);

        Review savedReview = reviewRepository.save(review);

        updateProductAverageScore(productId);

        return savedReview;
    }

    /**
     * Updates the product's average review score.
     *
     * @param productId the id of the product
     */
    private void updateProductAverageScore(Long productId) {
        Double averageScore = reviewRepository.getAverageScoreByProductId(productId);
        if (averageScore != null) {
            productService.updateProductAverageScore(productId, averageScore);
        }
    }

}

