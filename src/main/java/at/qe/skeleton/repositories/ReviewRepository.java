package at.qe.skeleton.repositories;

import at.qe.skeleton.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ReviewRepository extends AbstractRepository<Review, Long> {

    @Query("SELECT r FROM Review r WHERE r.productId = :productId " +
           "AND (:minRating IS NULL OR r.score >= :minRating) " +
           "AND (:maxRating IS NULL OR r.score <= :maxRating)")
    Page<Review> findByProductIdWithFilters(
            @Param("productId") Long productId,
            @Param("minRating") Integer minRating,
            @Param("maxRating") Integer maxRating,
            Pageable pageable
    );

    Optional<Review> findByUser_IdAndProductId(Long userId, Long productId);

    long countByProductId(Long productId);

    @Query("SELECT AVG(r.score) FROM Review r WHERE r.productId = :productId")
    Double getAverageScoreByProductId(@Param("productId") Long productId);
}

