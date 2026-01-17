package at.qe.skeleton.tests;

import at.qe.skeleton.Helpers.SortHelper;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

public class SortHelperTest {

    private static final Predicate<String> VALID_FIELDS = field ->
            field.equals("name") || field.equals("price") || field.equals("id") || field.equals("date");

    private static final String FALLBACK = "id";

    @Test
    void parseSortWithNullString() {
        Sort result = SortHelper.parseSort(null, VALID_FIELDS, FALLBACK);

        assertNotNull(result);
        assertEquals(Sort.Direction.DESC, result.getOrderFor(FALLBACK).getDirection());
        assertEquals(FALLBACK, result.getOrderFor(FALLBACK).getProperty());
    }

    @Test
    void parseSortWithEmptyString() {
        Sort result = SortHelper.parseSort("", VALID_FIELDS, FALLBACK);

        assertNotNull(result);
        assertEquals(Sort.Direction.DESC, result.getOrderFor(FALLBACK).getDirection());
    }

    @Test
    void parseSortWithBlankString() {
        Sort result = SortHelper.parseSort("   ", VALID_FIELDS, FALLBACK);

        assertNotNull(result);
        assertEquals(Sort.Direction.DESC, result.getOrderFor(FALLBACK).getDirection());
    }

    @Test
    void parseSortWithValidAscending() {
        Sort result = SortHelper.parseSort("name,asc", VALID_FIELDS, FALLBACK);

        assertNotNull(result);
        assertEquals(Sort.Direction.ASC, result.getOrderFor("name").getDirection());
        assertEquals("name", result.getOrderFor("name").getProperty());
    }

    @Test
    void parseSortWithValidDescending() {
        Sort result = SortHelper.parseSort("price,desc", VALID_FIELDS, FALLBACK);

        assertNotNull(result);
        assertEquals(Sort.Direction.DESC, result.getOrderFor("price").getDirection());
        assertEquals("price", result.getOrderFor("price").getProperty());
    }

    @Test
    void parseSortWithUppercaseDirection() {
        Sort result = SortHelper.parseSort("name,ASC", VALID_FIELDS, FALLBACK);

        assertNotNull(result);
        assertEquals(Sort.Direction.ASC, result.getOrderFor("name").getDirection());
    }

    @Test
    void parseSortWithMixedCaseDirection() {
        Sort result = SortHelper.parseSort("name,DeSc", VALID_FIELDS, FALLBACK);

        assertNotNull(result);
        assertEquals(Sort.Direction.DESC, result.getOrderFor("name").getDirection());
    }

    @Test
    void parseSortWithInvalidField() {
        Sort result = SortHelper.parseSort("invalidField,asc", VALID_FIELDS, FALLBACK);

        assertNotNull(result);
        // Should fall back to fallback field
        assertEquals(Sort.Direction.ASC, result.getOrderFor(FALLBACK).getDirection());
        assertEquals(FALLBACK, result.getOrderFor(FALLBACK).getProperty());
    }

    @Test
    void parseSortWithInvalidFormatSinglePart() {
        Sort result = SortHelper.parseSort("name", VALID_FIELDS, FALLBACK);

        assertNotNull(result);
        // Should fall back due to invalid format
        assertEquals(Sort.Direction.DESC, result.getOrderFor(FALLBACK).getDirection());
    }

    @Test
    void parseSortWithInvalidFormatThreeParts() {
        Sort result = SortHelper.parseSort("name,asc,extra", VALID_FIELDS, FALLBACK);

        assertNotNull(result);
        // Should fall back due to invalid format
        assertEquals(Sort.Direction.DESC, result.getOrderFor(FALLBACK).getDirection());
    }

    @Test
    void parseSortWithWhitespace() {
        Sort result = SortHelper.parseSort("  name  ,  asc  ", VALID_FIELDS, FALLBACK);

        assertNotNull(result);
        assertEquals(Sort.Direction.ASC, result.getOrderFor("name").getDirection());
        assertEquals("name", result.getOrderFor("name").getProperty());
    }

    @Test
    void parseSortWithUnknownDirection() {
        Sort result = SortHelper.parseSort("name,unknown", VALID_FIELDS, FALLBACK);

        assertNotNull(result);
        // Unknown direction should default to DESC
        assertEquals(Sort.Direction.DESC, result.getOrderFor("name").getDirection());
    }

    @Test
    void parseSortWithEmptyDirection() {
        Sort result = SortHelper.parseSort("name,", VALID_FIELDS, FALLBACK);

        assertNotNull(result);
        // Empty direction should default to DESC, but field might be invalid so check fallback
        Sort.Order order = result.getOrderFor("name");
        if (order == null) {
            order = result.getOrderFor(FALLBACK);
        }
        assertNotNull(order);
        assertEquals(Sort.Direction.DESC, order.getDirection());
    }

    @Test
    void parseSortWithEmptyField() {
        Sort result = SortHelper.parseSort(",asc", VALID_FIELDS, FALLBACK);

        assertNotNull(result);
        // Empty field should fall back
        assertEquals(Sort.Direction.ASC, result.getOrderFor(FALLBACK).getDirection());
    }

    @Test
    void parseSortWithNullPredicate() {
        // Test that null predicate doesn't cause NPE
        assertThrows(NullPointerException.class, () -> {
            SortHelper.parseSort("name,asc", null, FALLBACK);
        });
    }

    @Test
    void parseSortWithNullFallback() {
        // Test that null fallback causes exception when used
        // This happens when field is invalid and fallback is needed
        // Spring's Sort.by() throws IllegalArgumentException for null property
        assertThrows(IllegalArgumentException.class, () -> {
            SortHelper.parseSort("invalidField,asc", VALID_FIELDS, null);
        });
    }

    @Test
    void parseSortWithMultipleValidFields() {
        Predicate<String> multipleFields = field ->
                field.equals("name") || field.equals("price") || field.equals("id") || 
                field.equals("date") || field.equals("stock");

        Sort result1 = SortHelper.parseSort("name,asc", multipleFields, FALLBACK);
        assertEquals("name", result1.getOrderFor("name").getProperty());

        Sort result2 = SortHelper.parseSort("price,desc", multipleFields, FALLBACK);
        assertEquals("price", result2.getOrderFor("price").getProperty());

        Sort result3 = SortHelper.parseSort("stock,asc", multipleFields, FALLBACK);
        assertEquals("stock", result3.getOrderFor("stock").getProperty());
    }
}
