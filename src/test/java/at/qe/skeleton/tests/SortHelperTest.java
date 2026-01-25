package at.qe.skeleton.tests;

import at.qe.skeleton.Helpers.SortHelper;
import at.qe.skeleton.model.Userx;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;

import java.util.Objects;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class SortHelperTest {

    @Autowired
    private SortHelper sortHelper;

    private static final Predicate<String> VALID_FIELDS = field ->
            field.equals("name") || field.equals("price") || field.equals("id") || field.equals("date");

    private static final String FALLBACK = "id";

    // We pass an entity class for metamodel lookup (type detection for ignoreCase).
    // It does NOT need to contain all VALID_FIELDS; fields not found are treated as non-string.
    private static final Class<?> ENTITY = Userx.class;

    @Test
    void parseSortWithNullString() {
        Sort result = sortHelper.parseSort(null, ENTITY, VALID_FIELDS, FALLBACK);

        assertNotNull(result);
        assertNotNull(result.getOrderFor(FALLBACK));
        assertEquals(Sort.Direction.DESC, Objects.requireNonNull(result.getOrderFor(FALLBACK)).getDirection());
        assertEquals(FALLBACK, Objects.requireNonNull(result.getOrderFor(FALLBACK)).getProperty());
    }

    @Test
    void parseSortWithEmptyString() {
        Sort result = sortHelper.parseSort("", ENTITY, VALID_FIELDS, FALLBACK);

        assertNotNull(result);
        assertNotNull(result.getOrderFor(FALLBACK));
        assertEquals(Sort.Direction.DESC, Objects.requireNonNull(result.getOrderFor(FALLBACK)).getDirection());
    }

    @Test
    void parseSortWithBlankString() {
        Sort result = sortHelper.parseSort("   ", ENTITY, VALID_FIELDS, FALLBACK);

        assertNotNull(result);
        assertNotNull(result.getOrderFor(FALLBACK));
        assertEquals(Sort.Direction.DESC, Objects.requireNonNull(result.getOrderFor(FALLBACK)).getDirection());
    }

    @Test
    void parseSortWithValidAscending() {
        Sort result = sortHelper.parseSort("name,asc", ENTITY, VALID_FIELDS, FALLBACK);

        assertNotNull(result);
        assertNotNull(result.getOrderFor("name"));
        assertEquals(Sort.Direction.ASC, Objects.requireNonNull(result.getOrderFor("name")).getDirection());
        assertEquals("name", Objects.requireNonNull(result.getOrderFor("name")).getProperty());
    }

    @Test
    void parseSortWithValidDescending() {
        Sort result = sortHelper.parseSort("price,desc", ENTITY, VALID_FIELDS, FALLBACK);

        assertNotNull(result);
        assertNotNull(result.getOrderFor("price"));
        assertEquals(Sort.Direction.DESC, Objects.requireNonNull(result.getOrderFor("price")).getDirection());
        assertEquals("price", Objects.requireNonNull(result.getOrderFor("price")).getProperty());
    }

    @Test
    void parseSortWithUppercaseDirection() {
        Sort result = sortHelper.parseSort("name,ASC", ENTITY, VALID_FIELDS, FALLBACK);

        assertNotNull(result);
        assertNotNull(result.getOrderFor("name"));
        assertEquals(Sort.Direction.ASC, Objects.requireNonNull(result.getOrderFor("name")).getDirection());
    }

    @Test
    void parseSortWithMixedCaseDirection() {
        Sort result = sortHelper.parseSort("name,DeSc", ENTITY, VALID_FIELDS, FALLBACK);

        assertNotNull(result);
        assertNotNull(result.getOrderFor("name"));
        assertEquals(Sort.Direction.DESC, Objects.requireNonNull(result.getOrderFor("name")).getDirection());
    }

    @Test
    void parseSortWithInvalidField() {
        Sort result = sortHelper.parseSort("invalidField,asc", ENTITY, VALID_FIELDS, FALLBACK);

        assertNotNull(result);
        assertNotNull(result.getOrderFor(FALLBACK));
        // Should fall back to fallback field but keep direction
        assertEquals(Sort.Direction.ASC, Objects.requireNonNull(result.getOrderFor(FALLBACK)).getDirection());
        assertEquals(FALLBACK, Objects.requireNonNull(result.getOrderFor(FALLBACK)).getProperty());
    }

    @Test
    void parseSortWithInvalidFormatSinglePart() {
        Sort result = sortHelper.parseSort("name", ENTITY, VALID_FIELDS, FALLBACK);

        assertNotNull(result);
        assertNotNull(result.getOrderFor("name"));
        // default direction is DESC if direction part is missing
        assertEquals(Sort.Direction.DESC, Objects.requireNonNull(result.getOrderFor("name")).getDirection());
        assertEquals("name", Objects.requireNonNull(result.getOrderFor("name")).getProperty());
    }

    @Test
    void parseSortWithInvalidFormatThreeParts() {
        Sort result = sortHelper.parseSort("name,asc,extra", ENTITY, VALID_FIELDS, FALLBACK);

        assertNotNull(result);
        assertNotNull(result.getOrderFor("name"));
        // takes first two parts => asc
        assertEquals(Sort.Direction.ASC, Objects.requireNonNull(result.getOrderFor("name")).getDirection());
        assertEquals("name", Objects.requireNonNull(result.getOrderFor("name")).getProperty());
    }

    @Test
    void parseSortWithWhitespace() {
        Sort result = sortHelper.parseSort("  name  ,  asc  ", ENTITY, VALID_FIELDS, FALLBACK);

        assertNotNull(result);
        assertNotNull(result.getOrderFor("name"));
        assertEquals(Sort.Direction.ASC, Objects.requireNonNull(result.getOrderFor("name")).getDirection());
        assertEquals("name", Objects.requireNonNull(result.getOrderFor("name")).getProperty());
    }

    @Test
    void parseSortWithUnknownDirection() {
        Sort result = sortHelper.parseSort("name,unknown", ENTITY, VALID_FIELDS, FALLBACK);

        assertNotNull(result);
        assertNotNull(result.getOrderFor("name"));
        // Unknown direction should default to DESC
        assertEquals(Sort.Direction.DESC, Objects.requireNonNull(result.getOrderFor("name")).getDirection());
    }

    @Test
    void parseSortWithEmptyDirection() {
        Sort result = sortHelper.parseSort("name,", ENTITY, VALID_FIELDS, FALLBACK);

        assertNotNull(result);
        assertNotNull(result.getOrderFor("name"));
        // Empty direction defaults to DESC
        assertEquals(Sort.Direction.DESC, Objects.requireNonNull(result.getOrderFor("name")).getDirection());
    }

    @Test
    void parseSortWithEmptyField() {
        Sort result = sortHelper.parseSort(",asc", ENTITY, VALID_FIELDS, FALLBACK);

        assertNotNull(result);
        assertNotNull(result.getOrderFor(FALLBACK));
        // Empty field should fall back but keep direction
        assertEquals(Sort.Direction.ASC, Objects.requireNonNull(result.getOrderFor(FALLBACK)).getDirection());
    }

    @Test
    void parseSortWithNullPredicate() {
        // null predicate -> should throw NPE when tested
        assertThrows(NullPointerException.class, () ->
                sortHelper.parseSort("name,asc", ENTITY, null, FALLBACK)
        );
    }

    @Test
    void parseSortWithNullFallback() {
        // If fallback is null and needed, Sort.Order should reject it
        assertThrows(IllegalArgumentException.class, () ->
                sortHelper.parseSort("invalidField,asc", ENTITY, VALID_FIELDS, null)
        );
    }

    @Test
    void parseSortWithMultipleValidFields() {
        Predicate<String> multipleFields = field ->
                field.equals("name") || field.equals("price") || field.equals("id") ||
                        field.equals("date") || field.equals("stock");

        Sort result1 = sortHelper.parseSort("name,asc", ENTITY, multipleFields, FALLBACK);
        assertNotNull(result1.getOrderFor("name"));
        assertEquals("name", Objects.requireNonNull(result1.getOrderFor("name")).getProperty());

        Sort result2 = sortHelper.parseSort("price,desc", ENTITY, multipleFields, FALLBACK);
        assertNotNull(result2.getOrderFor("price"));
        assertEquals("price", Objects.requireNonNull(result2.getOrderFor("price")).getProperty());

        Sort result3 = sortHelper.parseSort("stock,asc", ENTITY, multipleFields, FALLBACK);
        assertNotNull(result3.getOrderFor("stock"));
        assertEquals("stock", Objects.requireNonNull(result3.getOrderFor("stock")).getProperty());
    }
}
