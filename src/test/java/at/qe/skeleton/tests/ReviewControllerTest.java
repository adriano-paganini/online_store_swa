package at.qe.skeleton.tests;

import at.qe.skeleton.configs.JwtConfig;
import at.qe.skeleton.configs.JwtTokenProvider;
import at.qe.skeleton.configs.TokenAuthenticationFilter;
import at.qe.skeleton.controllers.ReviewController;
import at.qe.skeleton.dtos.ReviewCreateDTO;
import at.qe.skeleton.dtos.ReviewDTO;
import at.qe.skeleton.mappers.ReviewMapper;
import at.qe.skeleton.model.Review;
import at.qe.skeleton.model.Userx;
import at.qe.skeleton.services.ReviewService;
import at.qe.skeleton.services.UserxService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@WebMvcTest(ReviewController.class)
@AutoConfigureMockMvc
public class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoSpyBean
    private TokenAuthenticationFilter tokenAuthenticationFilter;

    @MockitoBean
    private JwtConfig jwtConfig;

    @MockitoBean
    private UserxService userService;

    @MockitoBean
    private ReviewService reviewService;

    @MockitoBean
    private ReviewMapper reviewMapper;

    private Long testProductId;
    private Userx testUser;
    private Review testReview;

    @BeforeEach
    void setUp() throws Exception {
        testProductId = 10L;

        testUser = new Userx();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setFirstName("Test");
        testUser.setLastName("User");

        testReview = new Review();
        testReview.setId(1L);
        testReview.setUser(testUser);
        testReview.setProductId(testProductId);
        testReview.setScore(5);
        testReview.setContent("Great product!");
        testReview.setTimestamp(LocalDateTime.now());

        Mockito.doAnswer(invocation -> {
            HttpServletRequest request = invocation.getArgument(0);
            HttpServletResponse response = invocation.getArgument(1);
            FilterChain chain = invocation.getArgument(2);
            chain.doFilter(request, response);
            return null;
        }).when(tokenAuthenticationFilter).doFilterInternal(
                Mockito.any(HttpServletRequest.class),
                Mockito.any(HttpServletResponse.class),
                Mockito.any(FilterChain.class)
        );

        @SuppressWarnings("unchecked")
        Jws<Claims> mockJws = (Jws<Claims>) Mockito.mock(Jws.class);
        Claims mockClaims = Mockito.mock(Claims.class);
        Mockito.when(mockClaims.getSubject()).thenReturn("testuser");
        Mockito.when(mockJws.getPayload()).thenReturn(mockClaims);
        Mockito.when(jwtTokenProvider.validateTokenAndGetJws(Mockito.anyString()))
                .thenReturn(Optional.of(mockJws));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getProductReviews() throws Exception {
        int page = 0;
        int limit = 6;
        PageRequest pageable = PageRequest.of(page, limit);
        Page<Review> reviewPage = new PageImpl<>(List.of(testReview), pageable, 1);

        ReviewDTO reviewDTO = new ReviewDTO(
                testProductId,
                "Test User",
                5,
                "Great product!",
                testReview.getTimestamp()
        );

        Mockito.when(reviewService.getProductReviews(
                testProductId, page, limit, null, null, null))
                .thenReturn(reviewPage);
        Mockito.when(reviewMapper.mapTo(testReview)).thenReturn(reviewDTO);

        mockMvc.perform(MockMvcRequestBuilders.get("/products/{productId}/reviews", testProductId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].productId").value(testProductId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].authorName").value("Test User"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].score").value(5))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].content").value("Great product!"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.page").value(page))
                .andExpect(MockMvcResultMatchers.jsonPath("$.limit").value(limit))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalPages").value(1));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getProductReviewsWithPagination() throws Exception {
        int page = 1;
        int limit = 3;
        PageRequest pageable = PageRequest.of(page, limit);
        Page<Review> reviewPage = new PageImpl<>(List.of(), pageable, 0);

        Mockito.when(reviewService.getProductReviews(
                testProductId, page, limit, null, null, null))
                .thenReturn(reviewPage);

        mockMvc.perform(MockMvcRequestBuilders.get("/products/{productId}/reviews", testProductId)
                        .param("page", String.valueOf(page))
                        .param("limit", String.valueOf(limit)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.page").value(page))
                .andExpect(MockMvcResultMatchers.jsonPath("$.limit").value(limit))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(0));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getProductReviewsWithFilters() throws Exception {
        int page = 0;
        int limit = 6;
        Integer minRating = 4;
        Integer maxRating = 5;
        PageRequest pageable = PageRequest.of(page, limit);
        Page<Review> reviewPage = new PageImpl<>(List.of(testReview), pageable, 1);

        ReviewDTO reviewDTO = new ReviewDTO(
                testProductId,
                "Test User",
                5,
                "Great product!",
                testReview.getTimestamp()
        );

        Mockito.when(reviewService.getProductReviews(
                testProductId, page, limit, minRating, maxRating, null))
                .thenReturn(reviewPage);
        Mockito.when(reviewMapper.mapTo(testReview)).thenReturn(reviewDTO);

        mockMvc.perform(MockMvcRequestBuilders.get("/products/{productId}/reviews", testProductId)
                        .param("minRating", String.valueOf(minRating))
                        .param("maxRating", String.valueOf(maxRating)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.length()").value(1));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getProductReviewsWithSort() throws Exception {
        int page = 0;
        int limit = 6;
        String sort = "score,desc";
        PageRequest pageable = PageRequest.of(page, limit);
        Page<Review> reviewPage = new PageImpl<>(List.of(testReview), pageable, 1);

        ReviewDTO reviewDTO = new ReviewDTO(
                testProductId,
                "Test User",
                5,
                "Great product!",
                testReview.getTimestamp()
        );

        Mockito.when(reviewService.getProductReviews(
                testProductId, page, limit, null, null, sort))
                .thenReturn(reviewPage);
        Mockito.when(reviewMapper.mapTo(testReview)).thenReturn(reviewDTO);

        mockMvc.perform(MockMvcRequestBuilders.get("/products/{productId}/reviews", testProductId)
                        .param("sort", sort))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.length()").value(1));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getProductReviewsEmpty() throws Exception {
        int page = 0;
        int limit = 6;
        PageRequest pageable = PageRequest.of(page, limit);
        Page<Review> reviewPage = new PageImpl<>(List.of(), pageable, 0);

        Mockito.when(reviewService.getProductReviews(
                testProductId, page, limit, null, null, null))
                .thenReturn(reviewPage);

        mockMvc.perform(MockMvcRequestBuilders.get("/products/{productId}/reviews", testProductId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.length()").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(0));
    }

    @Test
    @WithMockUser(username = "testuser")
    void createReview() throws Exception {
        ReviewCreateDTO createDTO = new ReviewCreateDTO(5, "Excellent product!");

        ReviewDTO reviewDTO = new ReviewDTO(
                testProductId,
                "Test User",
                5,
                "Excellent product!",
                testReview.getTimestamp()
        );

        Mockito.when(reviewService.createReview(testProductId, createDTO)).thenReturn(testReview);
        Mockito.when(reviewMapper.mapTo(testReview)).thenReturn(reviewDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/products/{productId}/reviews", testProductId)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(createDTO)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.productId").value(testProductId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.authorName").value("Test User"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.score").value(5))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").value("Excellent product!"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void createReviewWithMinScore() throws Exception {
        ReviewCreateDTO createDTO = new ReviewCreateDTO(1, "Poor product");

        Review review = new Review();
        review.setId(1L);
        review.setUser(testUser);
        review.setProductId(testProductId);
        review.setScore(1);
        review.setContent("Poor product");
        review.setTimestamp(LocalDateTime.now());

        ReviewDTO reviewDTO = new ReviewDTO(
                testProductId,
                "Test User",
                1,
                "Poor product",
                review.getTimestamp()
        );

        Mockito.when(reviewService.createReview(testProductId, createDTO)).thenReturn(review);
        Mockito.when(reviewMapper.mapTo(review)).thenReturn(reviewDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/products/{productId}/reviews", testProductId)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(createDTO)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.score").value(1));
    }

    @Test
    @WithMockUser(username = "testuser")
    void createReviewWithMaxScore() throws Exception {
        ReviewCreateDTO createDTO = new ReviewCreateDTO(5, "Perfect product");

        ReviewDTO reviewDTO = new ReviewDTO(
                testProductId,
                "Test User",
                5,
                "Perfect product",
                testReview.getTimestamp()
        );

        Mockito.when(reviewService.createReview(testProductId, createDTO)).thenReturn(testReview);
        Mockito.when(reviewMapper.mapTo(testReview)).thenReturn(reviewDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/products/{productId}/reviews", testProductId)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(createDTO)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.score").value(5));
    }

    @Test
    @WithMockUser(username = "testuser")
    void createReviewInvalidScoreTooLow() throws Exception {
        ReviewCreateDTO createDTO = new ReviewCreateDTO(0, "Invalid score");

        mockMvc.perform(MockMvcRequestBuilders.post("/products/{productId}/reviews", testProductId)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(createDTO)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "testuser")
    void createReviewInvalidScoreTooHigh() throws Exception {
        ReviewCreateDTO createDTO = new ReviewCreateDTO(6, "Invalid score");

        mockMvc.perform(MockMvcRequestBuilders.post("/products/{productId}/reviews", testProductId)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(createDTO)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "testuser")
    void createReviewMissingContent() throws Exception {
        ReviewCreateDTO createDTO = new ReviewCreateDTO(5, "");

        mockMvc.perform(MockMvcRequestBuilders.post("/products/{productId}/reviews", testProductId)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(createDTO)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "testuser")
    void createReviewMissingScore() throws Exception {
        String json = "{\"content\":\"Great product!\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/products/{productId}/reviews", testProductId)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void getProductReviewsUnauthenticated() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/products/{productId}/reviews", testProductId))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    void createReviewUnauthenticated() throws Exception {
        ReviewCreateDTO createDTO = new ReviewCreateDTO(5, "Great product!");

        mockMvc.perform(MockMvcRequestBuilders.post("/products/{productId}/reviews", testProductId)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(createDTO)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "testuser")
    void getProductReviewsMultiplePages() throws Exception {
        int page = 0;
        int limit = 2;

        Review review1 = new Review();
        review1.setId(1L);
        review1.setUser(testUser);
        review1.setProductId(testProductId);
        review1.setScore(5);
        review1.setContent("Review 1");
        review1.setTimestamp(LocalDateTime.now());

        Review review2 = new Review();
        review2.setId(2L);
        review2.setUser(testUser);
        review2.setProductId(testProductId);
        review2.setScore(4);
        review2.setContent("Review 2");
        review2.setTimestamp(LocalDateTime.now());

        PageRequest pageable = PageRequest.of(page, limit);
        Page<Review> reviewPage = new PageImpl<>(List.of(review1, review2), pageable, 5);

        ReviewDTO reviewDTO1 = new ReviewDTO(testProductId, "Test User", 5, "Review 1", review1.getTimestamp());
        ReviewDTO reviewDTO2 = new ReviewDTO(testProductId, "Test User", 4, "Review 2", review2.getTimestamp());

        Mockito.when(reviewService.getProductReviews(
                testProductId, page, limit, null, null, null))
                .thenReturn(reviewPage);
        Mockito.when(reviewMapper.mapTo(review1)).thenReturn(reviewDTO1);
        Mockito.when(reviewMapper.mapTo(review2)).thenReturn(reviewDTO2);

        mockMvc.perform(MockMvcRequestBuilders.get("/products/{productId}/reviews", testProductId)
                        .param("page", String.valueOf(page))
                        .param("limit", String.valueOf(limit)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.length()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(5))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalPages").value(3));
    }
}

