package at.qe.skeleton.events;

import at.qe.skeleton.model.Product;
import at.qe.skeleton.model.SubscriptionType;

public class ProductRestockEvent extends ProductEvent<Integer>{
    public ProductRestockEvent(Product product, Integer oldValue, Integer newValue) {
        super(product, SubscriptionType.RESTOCK, oldValue, newValue);
    }
}
