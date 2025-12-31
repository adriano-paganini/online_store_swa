package at.qe.skeleton.services;

import at.qe.skeleton.dtos.ReviewCreateDTO;
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

import java.util.Optional;

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


        Sort sortObj = parseSort(sort);

        Pageable pageable = PageRequest.of(page, limit, sortObj);

        return reviewRepository.findByProductIdWithFilters(productId, minRating, maxRating, pageable);
    }


    @Transactional
    public Review createReview(Long productId, ReviewCreateDTO createDTO) {
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

        if (createDTO.score() < 1 || createDTO.score() > 5) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Score must be between 1 and 5");
        }

        Review review = new Review();
        review.setUser(currentUser);
        review.setProductId(productId);
        review.setScore(createDTO.score());
        review.setContent(createDTO.content().trim());

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

 
    private Sort parseSort(String sortString) {
        if (sortString == null || sortString.isEmpty()) {
            return Sort.by(Sort.Direction.DESC, "timestamp");
        }

        String[] parts = sortString.split(",");
        if (parts.length != 2) {
            return Sort.by(Sort.Direction.DESC, "timestamp");
        }

        String field = parts[0].trim();
        String direction = parts[1].trim().toLowerCase();

        Sort.Direction sortDirection = "asc".equals(direction) 
            ? Sort.Direction.ASC 
            : Sort.Direction.DESC;

        if (!isValidSortField(field)) {
            field = "timestamp";
        }

        return Sort.by(sortDirection, field);
    }


    private boolean isValidSortField(String field) {
        return "timestamp".equals(field) || "score".equals(field);
    }
}

