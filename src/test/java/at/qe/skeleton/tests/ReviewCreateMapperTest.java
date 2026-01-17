package at.qe.skeleton.tests;

import at.qe.skeleton.dtos.ReviewCreateDTO;
import at.qe.skeleton.mappers.ReviewCreateMapper;
import at.qe.skeleton.model.Review;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ReviewCreateMapperTest {

    private ReviewCreateMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ReviewCreateMapper();
    }

    @Test
    void mapFromWithCompleteDTO() {
        ReviewCreateDTO dto = new ReviewCreateDTO(5, "Great product!");

        Review review = mapper.mapFrom(dto);

        assertNotNull(review);
        assertEquals(5, review.getScore());
        assertEquals("Great product!", review.getContent());
    }

    @Test
    void mapFromWithNullDTO() {
        Review review = mapper.mapFrom(null);

        assertNull(review);
    }

    @Test
    void mapFromTrimsContent() {
        ReviewCreateDTO dto = new ReviewCreateDTO(5, "  Great product!  ");

        Review review = mapper.mapFrom(dto);

        assertNotNull(review);
        assertEquals("Great product!", review.getContent(), "Content should be trimmed");
    }

    @Test
    void mapFromWithNullContent() {
        ReviewCreateDTO dto = new ReviewCreateDTO(5, null);

        Review review = mapper.mapFrom(dto);

        assertNotNull(review);
        assertNull(review.getContent());
    }

    @Test
    void mapFromWithEmptyContent() {
        ReviewCreateDTO dto = new ReviewCreateDTO(5, "");

        Review review = mapper.mapFrom(dto);

        assertNotNull(review);
        assertEquals("", review.getContent());
    }

    @Test
    void mapFromWithWhitespaceOnlyContent() {
        ReviewCreateDTO dto = new ReviewCreateDTO(5, "   ");

        Review review = mapper.mapFrom(dto);

        assertNotNull(review);
        assertEquals("", review.getContent(), "Whitespace-only content should be trimmed to empty string");
    }

    @Test
    void mapFromWithMinimumScore() {
        ReviewCreateDTO dto = new ReviewCreateDTO(1, "Poor product");

        Review review = mapper.mapFrom(dto);

        assertNotNull(review);
        assertEquals(1, review.getScore());
    }

    @Test
    void mapFromWithMaximumScore() {
        ReviewCreateDTO dto = new ReviewCreateDTO(5, "Excellent product");

        Review review = mapper.mapFrom(dto);

        assertNotNull(review);
        assertEquals(5, review.getScore());
    }

    @Test
    void mapFromWithZeroScore() {
        ReviewCreateDTO dto = new ReviewCreateDTO(0, "Zero score");

        Review review = mapper.mapFrom(dto);

        assertNotNull(review);
        assertEquals(0, review.getScore(), "Mapper should preserve zero score (validation elsewhere)");
    }

    @Test
    void mapFromWithNegativeScore() {
        ReviewCreateDTO dto = new ReviewCreateDTO(-1, "Negative score");

        Review review = mapper.mapFrom(dto);

        assertNotNull(review);
        assertEquals(-1, review.getScore(), "Mapper should preserve negative score (validation elsewhere)");
    }

    @Test
    void mapFromWithLongContent() {
        String longContent = "A".repeat(1000);
        ReviewCreateDTO dto = new ReviewCreateDTO(5, longContent);

        Review review = mapper.mapFrom(dto);

        assertNotNull(review);
        assertEquals(longContent, review.getContent());
    }

    @Test
    void mapFromWithMultilineContent() {
        ReviewCreateDTO dto = new ReviewCreateDTO(5, "Line 1\nLine 2\nLine 3");

        Review review = mapper.mapFrom(dto);

        assertNotNull(review);
        assertEquals("Line 1\nLine 2\nLine 3", review.getContent());
    }
}
