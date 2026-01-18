package at.qe.skeleton.events;

import at.qe.skeleton.model.Product;
import at.qe.skeleton.model.SubscriptionType;

import java.time.format.DateTimeFormatter;

public class ProductPriceUpdateEvent extends ProductEvent<Double> {
    public ProductPriceUpdateEvent(Product product, Double oldValue, Double newValue) {
        super(product, SubscriptionType.PRICEUPDATE, oldValue, newValue);
    }

    @Override
    public String getPayloadSubjectLine() {
        DateTimeFormatter dateId = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        String formattedDate = super.getTimestamp().format(dateId);

        return String.format(java.util.Locale.US, "%s - The price of \"%s\" has been updated from %.2f to %.2f.",
                formattedDate,
                super.getProduct().getName(),
                super.getOldValue(),
                super.getNewValue()
        );
    }
}
