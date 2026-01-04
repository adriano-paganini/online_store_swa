package at.qe.skeleton.events;

import at.qe.skeleton.model.Product;
import at.qe.skeleton.model.SubscriptionType;

public class ProductDescriptionUpdateEvent extends ProductEvent<String>{
    public ProductDescriptionUpdateEvent(Product product, String oldValue, String newValue) {
        super(product, SubscriptionType.DESCRIPTIONUPDATE, oldValue, newValue);
    }
}
