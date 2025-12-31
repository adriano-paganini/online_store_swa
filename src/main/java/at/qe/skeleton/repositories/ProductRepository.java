package at.qe.skeleton.repositories;

import at.qe.skeleton.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends AbstractRepository<Product, Long> {


    @Query("SELECT p FROM Product p WHERE p.deleted = false " +
           "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
           "AND (:maxPrice IS NULL OR p.price <= :maxPrice) " +
           "AND (:inStock IS NULL OR (:inStock = true AND p.stock > 0) OR (:inStock = false AND p.stock = 0)) " +
           "AND (:minRating IS NULL OR p.avgScore >= :minRating)")
    Page<Product> findAllWithFilters(
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("inStock") Boolean inStock,
            @Param("minRating") Double minRating,
            Pageable pageable
    );


    @Query("SELECT p FROM Product p WHERE p.id = :id AND p.deleted = false")
    Optional<Product> findByIdAndNotDeleted(@Param("id") Long id);


    List<Product> findAll();

 
    Optional<Product> findById(Long id);
}

