package at.qe.skeleton.mappers;

import at.qe.skeleton.dtos.ReviewCreateDTO;
import at.qe.skeleton.model.Review;
import org.springframework.stereotype.Service;

@Service
public class ReviewCreateMapper {
    
    public Review mapFrom(ReviewCreateDTO dto) {
        if (dto == null) {
            return null;
        }

        Review review = new Review();
        review.setScore(dto.score());
        review.setContent(dto.content() != null ? dto.content().trim() : null);
        
        return review;
    }
}
