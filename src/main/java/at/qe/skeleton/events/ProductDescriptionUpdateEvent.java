package at.qe.skeleton.events;

import at.qe.skeleton.model.Product;
import at.qe.skeleton.model.SubscriptionType;

import java.time.format.DateTimeFormatter;

/**
 * Concrete implementation for product description changes.
 * * Hierarchy: Extends ProductEvent<String>, as descriptions are handled as text.
 */
public class ProductDescriptionUpdateEvent extends ProductEvent<String> {
    public ProductDescriptionUpdateEvent(Product product, String oldValue, String newValue) {
        // Uses the DESCRIPTIONUPDATE subscription type to identify this event
        super(product, SubscriptionType.DESCRIPTIONUPDATE, oldValue, newValue);
    }

    @Override
    public String getPayloadSubjectLine() {
        // Generates a simple notification string without showing the actual description text
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        String formattedDate = super.getTimestamp().format(formatter);

        return formattedDate + " - The description of \"" + super.getProduct().getName() + "\" has changed.";
    }
}
