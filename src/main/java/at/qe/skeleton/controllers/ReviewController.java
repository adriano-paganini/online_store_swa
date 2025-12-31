package at.qe.skeleton.controllers;

import at.qe.skeleton.dtos.PageResponseDTO;
import at.qe.skeleton.dtos.ReviewCreateDTO;
import at.qe.skeleton.dtos.ReviewDTO;
import at.qe.skeleton.mappers.ReviewMapper;
import at.qe.skeleton.model.Review;
import at.qe.skeleton.services.ReviewService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/products/{productId}/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final ReviewMapper reviewMapper;

    public ReviewController(ReviewService reviewService, ReviewMapper reviewMapper) {
        this.reviewService = reviewService;
        this.reviewMapper = reviewMapper;
    }

    /**
     * GET endpoint to retrieve paginated reviews for a product.
     */
    @GetMapping("")
    public ResponseEntity<PageResponseDTO<ReviewDTO>> getProductReviews(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int limit,
            @RequestParam(required = false) Integer minRating,
            @RequestParam(required = false) Integer maxRating,
            @RequestParam(required = false) String sort) {

        try {
            Page<Review> reviewPage = reviewService.getProductReviews(
                    productId, page, limit, minRating, maxRating, sort);

            List<ReviewDTO> reviewDTOs = reviewPage.getContent().stream()
                    .map(reviewMapper::mapTo)
                    .collect(Collectors.toList());

            PageResponseDTO<ReviewDTO> response = new PageResponseDTO<>(
                    reviewDTOs,
                    page,
                    limit,
                    reviewPage.getTotalElements(),
                    reviewPage.getTotalPages()
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error fetching reviews: " + e.getMessage());
        }
    }


    @PostMapping("")
    public ResponseEntity<ReviewDTO> createReview(
            @PathVariable Long productId,
            @Valid @RequestBody ReviewCreateDTO createDTO) {

        try {
            Review review = reviewService.createReview(productId, createDTO);
            ReviewDTO reviewDTO = reviewMapper.mapTo(review);
            return ResponseEntity.status(HttpStatus.CREATED).body(reviewDTO);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error creating review: " + e.getMessage());
        }
    }
}

