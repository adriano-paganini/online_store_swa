package at.qe.skeleton.events;

import at.qe.skeleton.model.Product;
import at.qe.skeleton.model.SubscriptionType;

import java.time.format.DateTimeFormatter;

/**
 * Concrete implementation for product restocking events.
 * * Hierarchy: Extends ProductEvent<Integer>, as stock quantities are whole numbers.
 */
public class ProductRestockEvent extends ProductEvent<Integer> {
    public ProductRestockEvent(Product product, Integer oldValue, Integer newValue) {
        // Uses the RESTOCK subscription type
        super(product, SubscriptionType.RESTOCK, oldValue, newValue);
    }

    @Override
    public String getPayloadSubjectLine() {
        // Handles singular/plural grammar for available pieces
        DateTimeFormatter dateId = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        String formattedDate = super.getTimestamp().format(dateId);

        return String.format("%s - \"%s\" has been restocked. %d %s available.",
                formattedDate,
                super.getProduct().getName(),
                super.getNewValue(),
                super.getNewValue() == 1 ? "piece is" : "pieces are"
        );
    }
}