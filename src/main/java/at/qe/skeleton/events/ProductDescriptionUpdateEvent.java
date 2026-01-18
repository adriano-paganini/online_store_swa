package at.qe.skeleton.events;

import at.qe.skeleton.model.Product;
import at.qe.skeleton.model.SubscriptionType;

import java.time.format.DateTimeFormatter;

public class ProductDescriptionUpdateEvent extends ProductEvent<String> {
    public ProductDescriptionUpdateEvent(Product product, String oldValue, String newValue) {
        super(product, SubscriptionType.DESCRIPTIONUPDATE, oldValue, newValue);
    }

    @Override
    public String getPayloadSubjectLine() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        String formattedDate = super.getTimestamp().format(formatter);

        return formattedDate + " - The description of \"" + super.getProduct().getName() + "\" has changed.";
    }
}
