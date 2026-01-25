package at.qe.skeleton.events;
/**
 * A generic wrapper for event data.
 * Implements PayloadInterface and encapsulates an object of type T.
 * * Hierarchy: This is the base class for all data-carrying objects in the event system.
 * @param <T> The specific type of data (must implement PayloadInterface).
 */
public class Payload<T extends PayloadInterface> implements PayloadInterface {
    // The actual data object (e.g., an Order or a Product)
    T payloadInfo;

    public Payload(T payloadInfo) {
        this.payloadInfo = payloadInfo;
    }

    @Override
    public String getPayloadSubjectLine() {
        // Delegates the subject line generation to the underlying data object
        return payloadInfo.getPayloadSubjectLine();
    }
    public T getPayloadInfo() {
        return payloadInfo;
    }
    public void setPayloadInfo(T payloadInfo){
        this.payloadInfo= payloadInfo;
    }
}
