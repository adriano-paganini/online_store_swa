package at.qe.skeleton.events;

import at.qe.skeleton.model.Product;
import at.qe.skeleton.model.SubscriptionType;

import java.time.format.DateTimeFormatter;

public class ProductRestockEvent extends ProductEvent<Integer> {
    public ProductRestockEvent(Product product, Integer oldValue, Integer newValue) {
        super(product, SubscriptionType.RESTOCK, oldValue, newValue);
    }

    @Override
    public String getMessage() {
        DateTimeFormatter dateId = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        String formattedDate = super.getTimestamp().format(dateId);

        return String.format("%s - \"%s\" has been restocked. %d %s available.",
                formattedDate,
                super.getProduct().getName(),
                super.getNewValue(),
                super.getNewValue() == 1 ? "piece is" : "pieces are"
        );
    }
}