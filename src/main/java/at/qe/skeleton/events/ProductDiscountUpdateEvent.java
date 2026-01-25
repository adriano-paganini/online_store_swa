package at.qe.skeleton.events;

import at.qe.skeleton.model.Product;
import at.qe.skeleton.model.SubscriptionType;

import java.time.format.DateTimeFormatter;

/**
 * Concrete implementation for product discount changes.
 * * Hierarchy: Extends ProductEvent<Double>, representing the discount value.
 */
public class ProductDiscountUpdateEvent extends ProductEvent<Double> {
    public ProductDiscountUpdateEvent(Product product, Double oldValue, Double newValue) {
        // Uses the DISCOUNTUPDATE subscription type
        super(product, SubscriptionType.DISCOUNTUPDATE, oldValue, newValue);
    }

    @Override
    public String getPayloadSubjectLine() {
        // Formats the message with the old and new discount percentages/values
        DateTimeFormatter dateId = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        String formattedDate = super.getTimestamp().format(dateId);

        return String.format(java.util.Locale.US, "%s - The discount of \"%s\" has been updated from %.2f to %.2f.",
                formattedDate,
                super.getProduct().getName(),
                super.getOldValue(),
                super.getNewValue()
        );
    }
}
