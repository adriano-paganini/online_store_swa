package at.qe.skeleton.events;

import at.qe.skeleton.model.Product;
import at.qe.skeleton.model.SubscriptionType;

import java.time.format.DateTimeFormatter;

public class ProductDiscountUpdateEvent extends ProductEvent<Double> {
    public ProductDiscountUpdateEvent(Product product, Double oldValue, Double newValue) {
        super(product, SubscriptionType.DISCOUNTUPDATE, oldValue, newValue);
    }

    @Override
    public String getMessage() {
        DateTimeFormatter dateId = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        String formattedDate = super.getTimestamp().format(dateId);

        return String.format("%s - The discount of \"%s\" has been updated from %.2f to %.2f.",
                formattedDate,
                super.getProduct().getName(),
                super.getOldValue(),
                super.getNewValue()
        );
    }
}
