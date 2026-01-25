package at.qe.skeleton.events;

import at.qe.skeleton.model.Product;
import at.qe.skeleton.model.Subscription;
import at.qe.skeleton.model.SubscriptionType;

import java.time.LocalDateTime;

/**
 * Abstract intermediate class for all product-related changes.
 * * Hierarchy: Extends Payload<Subscription> because product changes are typically
 * broadcasted to subscribers.
 * @param <T> The type of the value being changed (e.g., Double for price, String for name).
 */
public class ProductEvent<T> extends Payload<Subscription> {
    private final Product product;
    private final SubscriptionType subscriptionType; // Identifies the nature of the change
    private final T oldValue;
    private final T newValue;
    private final LocalDateTime timestamp;

    public ProductEvent(Product product, SubscriptionType subscriptionType, T oldValue, T newValue) {
        super(null); // Initializes the Payload base, specific subscriptions are handled later
        this.product = product;
        this.subscriptionType = subscriptionType;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.timestamp = LocalDateTime.now();
    }

    public Product getProduct(){
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
