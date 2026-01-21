package at.qe.skeleton.events;
/**
 * Base interface for all event data.
 * Ensures that every payload can provide a descriptive subject line for notifications.
 */
public interface PayloadInterface {
    String getPayloadSubjectLine();
}
