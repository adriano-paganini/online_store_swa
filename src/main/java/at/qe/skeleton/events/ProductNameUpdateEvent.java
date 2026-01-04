package at.qe.skeleton.events;

import at.qe.skeleton.model.Product;
import at.qe.skeleton.model.SubscriptionType;

import java.time.format.DateTimeFormatter;

public class ProductNameUpdateEvent extends ProductEvent<String> {
    public ProductNameUpdateEvent(Product product, String oldValue, String newValue) {
        super(product, SubscriptionType.NAMEUPDATE, oldValue, newValue);
    }

    @Override
    public String getMessage() {
        DateTimeFormatter dateId = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        String formattedDate = super.getTimestamp().format(dateId);

        return String.format("%s - The Name of \"%s\" has been updated to: %s.",
                formattedDate,
                super.getOldValue(),
                super.getNewValue()
        );
    }
}
