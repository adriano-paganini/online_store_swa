package at.qe.skeleton.events;

import at.qe.skeleton.model.Product;
import at.qe.skeleton.model.SubscriptionType;

import java.time.format.DateTimeFormatter;

/**
 * Concrete implementation for product name changes.
 * * Hierarchy: Extends ProductEvent<String>.
 */
public class ProductNameUpdateEvent extends ProductEvent<String> {
    public ProductNameUpdateEvent(Product product, String oldValue, String newValue) {
        // Uses the NAMEUPDATE subscription type
        super(product, SubscriptionType.NAMEUPDATE, oldValue, newValue);
    }

    @Override
    public String getPayloadSubjectLine() {
        // Notifies about the name change from the old name to the new name
        DateTimeFormatter dateId = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        String formattedDate = super.getTimestamp().format(dateId);

        return String.format("%s - The Name of \"%s\" has been updated to: %s.",
                formattedDate,
                super.getOldValue(),
                super.getNewValue()
        );
    }
}
