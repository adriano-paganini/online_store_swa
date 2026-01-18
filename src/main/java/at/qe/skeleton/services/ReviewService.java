package at.qe.skeleton.services;

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

import static at.qe.skeleton.Helpers.SortHelper.parseSort;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final AuthenticatedUserService authenticatedUserService;
    private final ProductService productService;

    public ReviewService(
            ReviewRepository reviewRepository,
            AuthenticatedUserService authenticatedUserService,
            ProductService productService) {
        this.reviewRepository = reviewRepository;
        this.authenticatedUserService = authenticatedUserService;
        this.productService = productService;
    }


    public Page<Review> getProductReviews(
            Long productId,
            int page,
            int limit,
            Integer minRating,
            Integer maxRating,
            String sort) {


        Sort sortObj = parseSort(sort,
                field -> Objects.equals("score", field),
                "timestamp");

        Pageable pageable = PageRequest.of(page, limit, sortObj);

        return reviewRepository.findByProductIdWithFilters(productId, minRating, maxRating, pageable);
    }


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


    private void updateProductAverageScore(Long productId) {
        Double averageScore = reviewRepository.getAverageScoreByProductId(productId);
        if (averageScore != null) {
            // TODO: Update Product entity's avgScore when Product entity is implemented
            // For now, this is a placeholder
            productService.updateProductAverageScore(productId, averageScore);
        }
    }

}

