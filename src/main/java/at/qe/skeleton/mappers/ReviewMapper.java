package at.qe.skeleton.mappers;

import at.qe.skeleton.dtos.ReviewDTO;
import at.qe.skeleton.model.Review;
import org.springframework.stereotype.Service;

@Service
public class ReviewMapper {

    public ReviewDTO mapTo(Review review) {
        if (review == null) {
            return null;
        }

        String authorName = review.getUser() != null 
            ? (review.getUser().getFirstName() + " " + review.getUser().getLastName()).trim()
            : "Anonymous";

        return new ReviewDTO(
            review.getProductId(),
            authorName,
            review.getScore(),
            review.getContent(),
            review.getTimestamp()
        );
    }
}

