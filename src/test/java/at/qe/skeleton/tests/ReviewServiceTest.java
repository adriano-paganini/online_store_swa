package at.qe.skeleton.tests;

import at.qe.skeleton.dtos.ReviewCreateDTO;
import at.qe.skeleton.model.Review;
import at.qe.skeleton.model.Userx;
import at.qe.skeleton.repositories.ReviewRepository;
import at.qe.skeleton.services.AuthenticatedUserService;
import at.qe.skeleton.services.ProductService;
import at.qe.skeleton.services.ReviewService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@SpringBootTest
public class ReviewServiceTest {

    @Autowired
    private ReviewService reviewService;

    @MockitoBean
    private ReviewRepository reviewRepository;

    @MockitoBean
    private AuthenticatedUserService authenticatedUserService;

    @MockitoBean
    private ProductService productService;

    private Userx testUser;
    private Long testProductId;

    @BeforeEach
    void setUp() {
        testUser = new Userx();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setFirstName("Test");
        testUser.setLastName("User");

        testProductId = 10L;

        Mockito.when(authenticatedUserService.getAuthenticatedUser()).thenReturn(testUser);
    }

    @Test
    @WithMockUser(username = "testuser")
    void testGetProductReviews() {
        int page = 0;
        int limit = 6;

        Review review1 = createTestReview(1L, testProductId, 5, "Great product!");
        Review review2 = createTestReview(2L, testProductId, 4, "Good quality");
        List<Review> reviews = List.of(review1, review2);
        Page<Review> reviewPage = new PageImpl<>(reviews, PageRequest.of(page, limit), 2);

        Mockito.when(reviewRepository.findByProductIdWithFilters(
                Mockito.eq(testProductId), Mockito.isNull(), Mockito.isNull(), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(reviewPage);

        Page<Review> result = reviewService.getProductReviews(
                testProductId, page, limit, null, null, "timestamp,desc");

        Assertions.assertNotNull(result, "Result should not be null");
        Assertions.assertEquals(2, result.getTotalElements(), "Should have 2 reviews");
        Assertions.assertEquals(2, result.getContent().size(), "Should return 2 reviews");
        Mockito.verify(reviewRepository).findByProductIdWithFilters(
                Mockito.eq(testProductId), Mockito.isNull(), Mockito.isNull(), ArgumentMatchers.any(Pageable.class));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testGetProductReviewsWithMinRating() {
        int page = 0;
        int limit = 6;
        Integer minRating = 4;

        Review review = createTestReview(1L, testProductId, 5, "Excellent!");
        Page<Review> reviewPage = new PageImpl<>(List.of(review), PageRequest.of(page, limit), 1);

        Mockito.when(reviewRepository.findByProductIdWithFilters(
                Mockito.eq(testProductId), Mockito.eq(minRating), Mockito.isNull(), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(reviewPage);

        Page<Review> result = reviewService.getProductReviews(
                testProductId, page, limit, minRating, null, "timestamp,desc");

        Assertions.assertNotNull(result, "Result should not be null");
        Assertions.assertEquals(1, result.getTotalElements(), "Should have 1 review");
        Mockito.verify(reviewRepository).findByProductIdWithFilters(
                Mockito.eq(testProductId), Mockito.eq(minRating), Mockito.isNull(), ArgumentMatchers.any(Pageable.class));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testGetProductReviewsWithMaxRating() {
        int page = 0;
        int limit = 6;
        Integer maxRating = 3;

        Review review = createTestReview(1L, testProductId, 2, "Not great");
        Page<Review> reviewPage = new PageImpl<>(List.of(review), PageRequest.of(page, limit), 1);

        Mockito.when(reviewRepository.findByProductIdWithFilters(
                Mockito.eq(testProductId), Mockito.isNull(), Mockito.eq(maxRating), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(reviewPage);

        Page<Review> result = reviewService.getProductReviews(
                testProductId, page, limit, null, maxRating, "timestamp,desc");

        Assertions.assertNotNull(result, "Result should not be null");
        Assertions.assertEquals(1, result.getTotalElements(), "Should have 1 review");
        Mockito.verify(reviewRepository).findByProductIdWithFilters(
                Mockito.eq(testProductId), Mockito.isNull(), Mockito.eq(maxRating), ArgumentMatchers.any(Pageable.class));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testGetProductReviewsWithSortByScore() {
        int page = 0;
        int limit = 6;

        Review review = createTestReview(1L, testProductId, 5, "Great!");
        Page<Review> reviewPage = new PageImpl<>(List.of(review), PageRequest.of(page, limit), 1);

        Mockito.when(reviewRepository.findByProductIdWithFilters(
                Mockito.eq(testProductId), Mockito.isNull(), Mockito.isNull(), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(reviewPage);

        Page<Review> result = reviewService.getProductReviews(
                testProductId, page, limit, null, null, "score,desc");

        Assertions.assertNotNull(result, "Result should not be null");
        Mockito.verify(reviewRepository).findByProductIdWithFilters(
                Mockito.eq(testProductId), Mockito.isNull(), Mockito.isNull(), ArgumentMatchers.any(Pageable.class));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testGetProductReviewsWithInvalidSort() {
        int page = 0;
        int limit = 6;

        Review review = createTestReview(1L, testProductId, 5, "Great!");
        Page<Review> reviewPage = new PageImpl<>(List.of(review), PageRequest.of(page, limit), 1);

        Mockito.when(reviewRepository.findByProductIdWithFilters(
                Mockito.eq(testProductId), Mockito.isNull(), Mockito.isNull(), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(reviewPage);

        Page<Review> result = reviewService.getProductReviews(
                testProductId, page, limit, null, null, "invalidField,desc");

        Assertions.assertNotNull(result, "Result should not be null");
        Mockito.verify(reviewRepository).findByProductIdWithFilters(
                Mockito.eq(testProductId), Mockito.isNull(), Mockito.isNull(), ArgumentMatchers.any(Pageable.class));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testCreateReview() {
        ReviewCreateDTO createDTO = new ReviewCreateDTO(5, "Excellent product!");

        Mockito.when(reviewRepository.findByUser_IdAndProductId(testUser.getId(), testProductId))
                .thenReturn(Optional.empty());
        Mockito.when(reviewRepository.getAverageScoreByProductId(testProductId))
                .thenReturn(5.0);
        Mockito.when(reviewRepository.save(Mockito.any(Review.class)))
                .thenAnswer(invocation -> {
                    Review review = invocation.getArgument(0);
                    review.setId(1L);
                    review.setTimestamp(LocalDateTime.now());
                    return review;
                });

        Review result = reviewService.createReview(testProductId, createDTO);

        Assertions.assertNotNull(result, "Review should not be null");
        Assertions.assertEquals(testUser, result.getUser(), "User should match");
        Assertions.assertEquals(testProductId, result.getProductId(), "Product ID should match");
        Assertions.assertEquals(5, result.getScore(), "Score should match");
        Assertions.assertEquals("Excellent product!", result.getContent(), "Content should match");

        ArgumentCaptor<Review> reviewCaptor = ArgumentCaptor.forClass(Review.class);
        Mockito.verify(reviewRepository).save(reviewCaptor.capture());
        Review savedReview = reviewCaptor.getValue();
        Assertions.assertEquals(testUser, savedReview.getUser());
        Assertions.assertEquals(testProductId, savedReview.getProductId());
        Assertions.assertEquals(5, savedReview.getScore());
        Assertions.assertEquals("Excellent product!", savedReview.getContent());

        Mockito.verify(productService).updateProductAverageScore(testProductId, 5.0);
    }

    @Test
    @WithMockUser(username = "testuser")
    void testCreateReviewWithContentTrim() {
        ReviewCreateDTO createDTO = new ReviewCreateDTO(4, "  Good product  ");

        Mockito.when(reviewRepository.findByUser_IdAndProductId(testUser.getId(), testProductId))
                .thenReturn(Optional.empty());
        Mockito.when(reviewRepository.getAverageScoreByProductId(testProductId))
                .thenReturn(4.0);
        Mockito.when(reviewRepository.save(Mockito.any(Review.class)))
                .thenAnswer(invocation -> {
                    Review review = invocation.getArgument(0);
                    review.setId(1L);
                    review.setTimestamp(LocalDateTime.now());
                    return review;
                });

        Review result = reviewService.createReview(testProductId, createDTO);

        Assertions.assertNotNull(result, "Review should not be null");
        ArgumentCaptor<Review> reviewCaptor = ArgumentCaptor.forClass(Review.class);
        Mockito.verify(reviewRepository).save(reviewCaptor.capture());
        Review savedReview = reviewCaptor.getValue();
        Assertions.assertEquals("Good product", savedReview.getContent(), "Content should be trimmed");
    }

    @Test
    @WithMockUser(username = "testuser")
    void testCreateReviewDuplicate() {
        ReviewCreateDTO createDTO = new ReviewCreateDTO(5, "Great product!");

        Review existingReview = createTestReview(1L, testProductId, 4, "Previous review");

        Mockito.when(reviewRepository.findByUser_IdAndProductId(testUser.getId(), testProductId))
                .thenReturn(Optional.of(existingReview));

        Assertions.assertThrows(ResponseStatusException.class, () -> {
            reviewService.createReview(testProductId, createDTO);
        }, "Should throw exception when user already has a review");

        Mockito.verify(reviewRepository, Mockito.never()).save(Mockito.any(Review.class));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testCreateReviewScoreTooLow() {
        ReviewCreateDTO createDTO = new ReviewCreateDTO(0, "Bad product");

        Mockito.when(reviewRepository.findByUser_IdAndProductId(testUser.getId(), testProductId))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(ResponseStatusException.class, () -> {
            reviewService.createReview(testProductId, createDTO);
        }, "Should throw exception when score is too low");

        Mockito.verify(reviewRepository, Mockito.never()).save(Mockito.any(Review.class));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testCreateReviewScoreTooHigh() {
        ReviewCreateDTO createDTO = new ReviewCreateDTO(6, "Amazing product");

        Mockito.when(reviewRepository.findByUser_IdAndProductId(testUser.getId(), testProductId))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(ResponseStatusException.class, () -> {
            reviewService.createReview(testProductId, createDTO);
        }, "Should throw exception when score is too high");

        Mockito.verify(reviewRepository, Mockito.never()).save(Mockito.any(Review.class));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testCreateReviewScoreBoundary1() {
        ReviewCreateDTO createDTO = new ReviewCreateDTO(1, "Poor product");

        Mockito.when(reviewRepository.findByUser_IdAndProductId(testUser.getId(), testProductId))
                .thenReturn(Optional.empty());
        Mockito.when(reviewRepository.getAverageScoreByProductId(testProductId))
                .thenReturn(1.0);
        Mockito.when(reviewRepository.save(Mockito.any(Review.class)))
                .thenAnswer(invocation -> {
                    Review review = invocation.getArgument(0);
                    review.setId(1L);
                    review.setTimestamp(LocalDateTime.now());
                    return review;
                });

        Review result = reviewService.createReview(testProductId, createDTO);

        Assertions.assertNotNull(result, "Review should be created");
        Assertions.assertEquals(1, result.getScore(), "Score should be 1");
        Mockito.verify(reviewRepository).save(Mockito.any(Review.class));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testCreateReviewScoreBoundary5() {
        ReviewCreateDTO createDTO = new ReviewCreateDTO(5, "Excellent product");

        Mockito.when(reviewRepository.findByUser_IdAndProductId(testUser.getId(), testProductId))
                .thenReturn(Optional.empty());
        Mockito.when(reviewRepository.getAverageScoreByProductId(testProductId))
                .thenReturn(5.0);
        Mockito.when(reviewRepository.save(Mockito.any(Review.class)))
                .thenAnswer(invocation -> {
                    Review review = invocation.getArgument(0);
                    review.setId(1L);
                    review.setTimestamp(LocalDateTime.now());
                    return review;
                });

        Review result = reviewService.createReview(testProductId, createDTO);

        Assertions.assertNotNull(result, "Review should be created");
        Assertions.assertEquals(5, result.getScore(), "Score should be 5");
    }

    @Test
    void testCreateReviewUnauthenticated() {
        ReviewCreateDTO createDTO = new ReviewCreateDTO(5, "Great product!");

        Mockito.when(authenticatedUserService.getAuthenticatedUser()).thenReturn(null);

        Assertions.assertThrows(ResponseStatusException.class, () -> {
            reviewService.createReview(testProductId, createDTO);
        }, "Should throw exception when user is not authenticated");

        Mockito.verify(reviewRepository, Mockito.never()).save(Mockito.any(Review.class));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testCreateReviewUpdatesProductAverageScore() {
        ReviewCreateDTO createDTO = new ReviewCreateDTO(4, "Good product");

        Mockito.when(reviewRepository.findByUser_IdAndProductId(testUser.getId(), testProductId))
                .thenReturn(Optional.empty());
        Mockito.when(reviewRepository.getAverageScoreByProductId(testProductId))
                .thenReturn(4.5);
        Mockito.when(reviewRepository.save(Mockito.any(Review.class)))
                .thenAnswer(invocation -> {
                    Review review = invocation.getArgument(0);
                    review.setId(1L);
                    review.setTimestamp(LocalDateTime.now());
                    return review;
                });

        reviewService.createReview(testProductId, createDTO);

        Mockito.verify(productService).updateProductAverageScore(testProductId, 4.5);
    }

    @Test
    @WithMockUser(username = "testuser")
    void testCreateReviewDoesNotUpdateWhenAverageIsNull() {
        ReviewCreateDTO createDTO = new ReviewCreateDTO(4, "Good product");

        Mockito.when(reviewRepository.findByUser_IdAndProductId(testUser.getId(), testProductId))
                .thenReturn(Optional.empty());
        Mockito.when(reviewRepository.getAverageScoreByProductId(testProductId))
                .thenReturn(null);
        Mockito.when(reviewRepository.save(Mockito.any(Review.class)))
                .thenAnswer(invocation -> {
                    Review review = invocation.getArgument(0);
                    review.setId(1L);
                    review.setTimestamp(LocalDateTime.now());
                    return review;
                });

        reviewService.createReview(testProductId, createDTO);

        Mockito.verify(productService, Mockito.never()).updateProductAverageScore(
                Mockito.anyLong(), Mockito.anyDouble());
    }

    @Test
    @WithMockUser(username = "testuser")
    void testGetProductReviewsWithSortAscending() {
        int page = 0;
        int limit = 6;

        Review review = createTestReview(1L, testProductId, 3, "Average");
        Page<Review> reviewPage = new PageImpl<>(List.of(review), PageRequest.of(page, limit), 1);

        Mockito.when(reviewRepository.findByProductIdWithFilters(
                Mockito.eq(testProductId), Mockito.isNull(), Mockito.isNull(), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(reviewPage);

        Page<Review> result = reviewService.getProductReviews(
                testProductId, page, limit, null, null, "score,asc");

        Assertions.assertNotNull(result, "Result should not be null");
        Mockito.verify(reviewRepository).findByProductIdWithFilters(
                Mockito.eq(testProductId), Mockito.isNull(), Mockito.isNull(), ArgumentMatchers.any(Pageable.class));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testGetProductReviewsWithNullSort() {
        int page = 0;
        int limit = 6;

        Review review = createTestReview(1L, testProductId, 4, "Good");
        Page<Review> reviewPage = new PageImpl<>(List.of(review), PageRequest.of(page, limit), 1);

        Mockito.when(reviewRepository.findByProductIdWithFilters(
                Mockito.eq(testProductId), Mockito.isNull(), Mockito.isNull(), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(reviewPage);

        Page<Review> result = reviewService.getProductReviews(
                testProductId, page, limit, null, null, null);

        Assertions.assertNotNull(result, "Result should not be null");
        Mockito.verify(reviewRepository).findByProductIdWithFilters(
                Mockito.eq(testProductId), Mockito.isNull(), Mockito.isNull(), ArgumentMatchers.any(Pageable.class));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testGetProductReviewsWithEmptySort() {
        int page = 0;
        int limit = 6;

        Review review = createTestReview(1L, testProductId, 4, "Good");
        Page<Review> reviewPage = new PageImpl<>(List.of(review), PageRequest.of(page, limit), 1);

        Mockito.when(reviewRepository.findByProductIdWithFilters(
                Mockito.eq(testProductId), Mockito.isNull(), Mockito.isNull(), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(reviewPage);

        Page<Review> result = reviewService.getProductReviews(
                testProductId, page, limit, null, null, "");

        Assertions.assertNotNull(result, "Result should not be null");
        Mockito.verify(reviewRepository).findByProductIdWithFilters(
                Mockito.eq(testProductId), Mockito.isNull(), Mockito.isNull(), ArgumentMatchers.any(Pageable.class));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testGetProductReviewsWithInvalidSortFormat() {
        int page = 0;
        int limit = 6;

        Review review = createTestReview(1L, testProductId, 4, "Good");
        Page<Review> reviewPage = new PageImpl<>(List.of(review), PageRequest.of(page, limit), 1);

        Mockito.when(reviewRepository.findByProductIdWithFilters(
                Mockito.eq(testProductId), Mockito.isNull(), Mockito.isNull(), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(reviewPage);

        Page<Review> result = reviewService.getProductReviews(
                testProductId, page, limit, null, null, "invalidFormat");

        Assertions.assertNotNull(result, "Result should not be null");
        Mockito.verify(reviewRepository).findByProductIdWithFilters(
                Mockito.eq(testProductId), Mockito.isNull(), Mockito.isNull(), ArgumentMatchers.any(Pageable.class));
    }

    private Review createTestReview(Long id, Long productId, Integer score, String content) {
        Review review = new Review();
        review.setId(id);
        review.setUser(testUser);
        review.setProductId(productId);
        review.setScore(score);
        review.setContent(content);
        review.setTimestamp(LocalDateTime.now());
        return review;
    }
}

