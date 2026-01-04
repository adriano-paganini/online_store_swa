package at.qe.skeleton.events;

import at.qe.skeleton.model.Product;
import at.qe.skeleton.model.SubscriptionType;

public class ProductDiscountUpdateEvent extends ProductEvent<Double>{
    public ProductDiscountUpdateEvent(Product product, Double oldValue, Double newValue) {
        super(product, SubscriptionType.DISCOUNTUPDATE, oldValue, newValue);
    }
}
