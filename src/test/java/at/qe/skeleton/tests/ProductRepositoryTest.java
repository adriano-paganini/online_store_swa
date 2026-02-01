package at.qe.skeleton.tests;

import at.qe.skeleton.model.Product;
import at.qe.skeleton.repositories.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.*;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void findAllWithFilters_searchNull_returnsAllNonDeletedAndRespectsPageAndSort() {
        // Arrange: insert products (because test-data.sql has no products)
        Product p1 = new Product();
        p1.setDeleted(false);
        p1.setName("Alpha");
        p1.setDescription("first");
        p1.setPrice(10.0);
        p1.setStock(5);
        p1.setAvgScore(4.0);
        em.persist(p1);

        Product p2 = new Product();
        p2.setDeleted(false);
        p2.setName("Bravo");
        p2.setDescription("second");
        p2.setPrice(20.0);
        p2.setStock(0);
        p2.setAvgScore(3.0);
        em.persist(p2);

        Product deleted = new Product();
        deleted.setDeleted(true);
        deleted.setName("ShouldNotAppear");
        deleted.setDescription("deleted");
        deleted.setPrice(5.0);
        deleted.setStock(1);
        deleted.setAvgScore(5.0);
        em.persist(deleted);

        em.flush();

        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());

        // Act
        Page<Product> page = productRepository.findAllWithFilters(
                null, null, null, null, null, pageable
        );

        // Assert
        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getContent()).extracting(Product::getName)
                .containsExactly("Alpha", "Bravo"); // deterministic due to Sort.by("id")
    }

    @Test
    void findAllWithFilters_searchFiltersByNameOrDescription_caseInsensitive_andIgnoresSpaces() {
        // Arrange
        Product matchInName = new Product();
        matchInName.setDeleted(false);
        matchInName.setName("Foo   Bar");              // extra spaces
        matchInName.setDescription("something else");
        matchInName.setPrice(10.0);
        matchInName.setStock(3);
        matchInName.setAvgScore(4.0);
        em.persist(matchInName);

        Product matchInDescription = new Product();
        matchInDescription.setDeleted(false);
        matchInDescription.setName("Other");
        matchInDescription.setDescription("xx fOoBaR yy"); // different casing, no spaces
        matchInDescription.setPrice(11.0);
        matchInDescription.setStock(1);
        matchInDescription.setAvgScore(4.0);
        em.persist(matchInDescription);

        Product nonMatch = new Product();
        nonMatch.setDeleted(false);
        nonMatch.setName("Nope");
        nonMatch.setDescription("nothing relevant");
        nonMatch.setPrice(12.0);
        nonMatch.setStock(2);
        nonMatch.setAvgScore(4.0);
        em.persist(nonMatch);

        em.flush();

        // Force deterministic order for assertions
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());

        // Act: search with spaces + different case
        Page<Product> page = productRepository.findAllWithFilters(
                null, null, null, null,
                "  foO   bAr  ",
                pageable
        );

        // Assert: only the 2 matching products
        assertThat(page.getTotalElements()).isEqualTo(2);

        // Also assert order is preserved according to sort (id asc)
        assertThat(page.getContent()).extracting(Product::getId)
                .containsExactly(matchInName.getId(), matchInDescription.getId());

        // And sanity-check they really match (name or description)
        assertThat(page.getContent()).extracting(Product::getName)
                .containsExactly("Foo   Bar", "Other");
    }
}
