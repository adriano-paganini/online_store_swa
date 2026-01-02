package at.qe.skeleton.events;

import at.qe.skeleton.model.Product;

public class ProductRestockEvent extends ProductEvent<Integer>{
    public ProductRestockEvent(Product product, Integer oldValue, Integer newValue) {
        super(product, SubscriptionType.RESTOCK, oldValue, newValue);
    }
}
