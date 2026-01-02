package at.qe.skeleton.events;

import at.qe.skeleton.model.Product;

public class ProductNameUpdateEvent extends ProductEvent<String>{
    public ProductNameUpdateEvent(Product product, String oldValue, String newValue) {
        super(product, SubscriptionType.NAMEUPDATE, oldValue, newValue);
    }
}
