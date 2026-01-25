package at.qe.skeleton.Helpers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.metamodel.Attribute;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@Component
public class SortHelper {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Parses a sort string like:
     *  - "username,asc"
     *  - "firstName,desc"
     *  - "username,asc;id,desc"   (multi-sort, separated by ';')
     *
     * For each field, it checks:
     *  - allowed via isValidField predicate
     *  - attribute type via JPA metamodel (String -> ignoreCase enabled)
     *
     * If sortString is invalid/blank, or a field is illegal, it falls back to `fallback`.
     */
    public Sort parseSort(String sortString,
                          Class<?> entityClass,
                          Predicate<String> isValidField,
                          String fallback) {

        // No sort -> fallback
        if (sortString == null || sortString.isBlank()) {
            return Sort.by(buildOrder(entityClass, Sort.Direction.DESC, fallback));
        }

        // Support multiple sort clauses separated by ';'
        String[] clauses = sortString.split(";");
        List<Sort.Order> orders = new ArrayList<>();

        for (String clause : clauses) {
            if (clause == null || clause.isBlank()) {
                continue;
            }

            String[] parts = clause.split(",");
            if (parts.length < 1) {
                continue;
            }

            String field = parts[0].trim();
            String directionRaw = (parts.length >= 2 ? parts[1].trim().toLowerCase() : "desc");

            Sort.Direction direction = "asc".equals(directionRaw)
                    ? Sort.Direction.ASC
                    : Sort.Direction.DESC;

            // validate field -> fallback if not allowed
            if (!isValidField.test(field)) {
                field = fallback;
            }

            orders.add(buildOrder(entityClass, direction, field));
        }

        // If everything was garbage, fallback
        if (orders.isEmpty()) {
            return Sort.by(buildOrder(entityClass, Sort.Direction.DESC, fallback));
        }

        return Sort.by(orders);
    }

    /**
     * Builds a Sort.Order and applies ignoreCase() ONLY if the attribute is String.
     * This prevents "lower(id)" crashes for Long/Number fields.
     */
    private Sort.Order buildOrder(Class<?> entityClass, Sort.Direction direction, String field) {
        Sort.Order order = new Sort.Order(direction, field);

        if (isStringAttribute(entityClass, field)) {
            order = order.ignoreCase();
        }

        return order;
    }

    /**
     * Uses JPA metamodel to detect attribute type reliably.
     */
    private boolean isStringAttribute(Class<?> entityClass, String field) {
        try {
            Attribute<?, ?> attr = entityManager.getMetamodel()
                    .entity(entityClass)
                    .getAttribute(field);

            return String.class.equals(attr.getJavaType());
        } catch (IllegalArgumentException ex) {
            // unknown attribute -> treat as non-string (no ignoreCase)
            return false;
        }
    }
}
