package at.qe.skeleton.events;

import at.qe.skeleton.model.Product;
import at.qe.skeleton.model.SubscriptionType;

import java.time.LocalDateTime;

public class ProductEvent<T> {
    private final Product product;
    private final SubscriptionType subscriptionType;
    private final T oldValue;
    private final T newValue;
    private final LocalDateTime timestamp;


    public ProductEvent(Product product, SubscriptionType subscriptionType, T oldValue, T newValue) {
        this.product = product;
        this.subscriptionType = subscriptionType;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.timestamp = LocalDateTime.now();
    }

    public Product getProduct() {
        return product;
    }

    public SubscriptionType getSubscriptionType() {
        return subscriptionType;
    }

    public T getOldValue() {
        return oldValue;
    }

    public T getNewValue() {
        return newValue;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
