package at.qe.skeleton.Helpers;

import org.springframework.data.domain.Sort;

import java.util.function.Predicate;

public class SortHelper {

    /**
     * @param sortString The raw string (e.g., "name,asc")
     * @param isValidField A function that returns true if the field name is allowed
     * @param fallback The fallback, if an illegal field name is selected
     */
    public static Sort parseSort(String sortString, Predicate<String> isValidField, String fallback) {
        if (sortString == null || sortString.isBlank()) {
            return Sort.by(Sort.Direction.DESC, fallback);
        }

        String[] parts = sortString.split(",");
        if (parts.length != 2) {
            return Sort.by(Sort.Direction.DESC, fallback);
        }

        String field = parts[0].trim();
        String direction = parts[1].trim().toLowerCase();

        Sort.Direction sortDirection = "asc".equals(direction)
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        if (!isValidField.test(field)) {
            field = fallback;
        }

        return Sort.by(sortDirection, field);
    }
}