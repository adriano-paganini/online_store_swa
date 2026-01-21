package at.qe.skeleton.events;

import at.qe.skeleton.model.Order;

import static at.qe.skeleton.Helpers.OrderEmailComposer.composePlainText;

/**
 * Specialized payload for completed orders.
 * * Hierarchy: Extends Payload<Order>, treating the Order model as the data source.
 */
public class OrderCompletionEvent extends Payload<Order> {

    public OrderCompletionEvent(Order payloadInfo) {
        super(payloadInfo);
    }

    @Override
    public String getPayloadSubjectLine() {
        // Uses a helper to convert the Order object into a human-readable string
        return composePlainText(getPayloadInfo());
    }
}
