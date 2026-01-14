package at.qe.skeleton.events;

import at.qe.skeleton.model.Order;

import static at.qe.skeleton.Helpers.OrderEmailComposer.composePlainText;

public class OrderCompletionEvent extends Payload<Order>{

    public OrderCompletionEvent(Order payloadInfo) {
        super(payloadInfo);
    }

    @Override
    public String getPayloadSubjectLine(){
        return composePlainText(getPayloadInfo());
    }

}
