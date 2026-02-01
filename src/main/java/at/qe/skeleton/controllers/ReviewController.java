package at.qe.skeleton.controllers;

import at.qe.skeleton.dtos.PageResponseDTO;
import at.qe.skeleton.dtos.ReviewCreateDTO;
import at.qe.skeleton.dtos.ReviewDTO;
import at.qe.skeleton.mappers.ReviewCreateMapper;
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

/**
 * REST controller for managing product reviews.
 *
 * <p>
 * Provides endpoints to retrieve and create reviews associated with a
 * specific product. Review creation is restricted to authenticated users.
 * </p>
 */
@RestController
@RequestMapping("/products/{productId}/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final ReviewMapper reviewMapper;
    private final ReviewCreateMapper reviewCreateMapper;

    public ReviewController(ReviewService reviewService, ReviewMapper reviewMapper, ReviewCreateMapper reviewCreateMapper) {
        this.reviewService = reviewService;
        this.reviewMapper = reviewMapper;
        this.reviewCreateMapper = reviewCreateMapper;
    }

    /**
     * Retrieves a paginated list of reviews for a product.
     *
     * <p>Supports optional filtering by rating and sorting.</p>
     *
     * <p>Possible responses:</p>
     * <ul>
     *   <li>200 OK - reviews successfully retrieved</li>
     *   <li>400 Bad Request - invalid query parameters</li>
     * </ul>
     *
     * @return paginated list of reviews
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


    /**
     * Creates a new review for a product.
     *
     * <p>Each user may only create one review per product.</p>
     *
     * <p>Possible responses:</p>
     * <ul>
     *   <li>201 Created - review successfully created</li>
     *   <li>400 Bad Request - invalid review data</li>
     *   <li>409 Conflict - user has already reviewed this product</li>
     * </ul>
     *
     * @param productId identifier of the product
     * @param createDTO review data to create
     * @return the newly created review
     */
    @PostMapping("")
    public ResponseEntity<ReviewDTO> createReview(
            @PathVariable Long productId,
            @Valid @RequestBody ReviewCreateDTO createDTO) {

        try {
            Review review = reviewCreateMapper.mapFrom(createDTO);
            Review createdReview = reviewService.createReview(productId, review);
            ReviewDTO reviewDTO = reviewMapper.mapTo(createdReview);
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

