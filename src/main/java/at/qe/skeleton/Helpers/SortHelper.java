package at.qe.skeleton.Helpers;

import org.springframework.data.domain.Sort;

import java.util.function.Predicate;

/**
 * Utility class for parsing and validating sort strings provided by the frontend.
 * * <p>The primary function {@code parseSort} processes raw strings (e.g., "name,asc")
 * and ensures that sorting fields are permitted based on a provided predicate.</p>
 * * <ul>
 * <li><b>sortString:</b> The raw input string from the request (e.g., "name,asc").</li>
 * <li><b>isValidField:</b> A function/predicate that returns true if the field name is allowed.</li>
 * <li><b>fallback:</b> The default field used if an illegal or missing field name is selected.</li>
 * </ul>
 */
public class SortHelper {

    private SortHelper(){}

    public static Sort parseSort(String sortString, Predicate<String> isValidField, String fallback) {
        // If no valid information is given, fallback to a base sort
        if (sortString == null || sortString.isBlank()) {
            return Sort.by(Sort.Direction.DESC, fallback);
        }

        // Extract arguments from sortString and sanity check. Falling back to base sort if needed.
        String[] parts = sortString.split(",");
        if (parts.length != 2) {
            return Sort.by(Sort.Direction.DESC, fallback);
        }

        // Extract the field and sorting-direction
        String field = parts[0].trim();
        String direction = parts[1].trim().toLowerCase();

        // Check if the direction is valid. If not, set to DESC
        Sort.Direction sortDirection = "asc".equals(direction)
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        // Use the isValidField function. If it fails, use the fallback.
        if (!isValidField.test(field)) {
            field = fallback;
        }

        // Return the extracted sort object
        return Sort.by(sortDirection, field);
    }
}