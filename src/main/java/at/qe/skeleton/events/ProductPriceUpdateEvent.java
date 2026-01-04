package at.qe.skeleton.events;

import at.qe.skeleton.model.Product;
import at.qe.skeleton.model.SubscriptionType;

public class ProductPriceUpdateEvent extends ProductEvent<Double>{
    public ProductPriceUpdateEvent(Product product, Double oldValue, Double newValue) {
        super(product, SubscriptionType.PRICEUPDATE, oldValue, newValue);
    }
}
