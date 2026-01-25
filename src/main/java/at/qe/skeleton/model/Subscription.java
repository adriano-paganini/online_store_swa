package at.qe.skeleton.model;

import at.qe.skeleton.events.PayloadInterface;
import jakarta.persistence.*;
import org.springframework.data.domain.Persistable;
import org.springframework.lang.Nullable;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

/**
 * Entity representing a user's subscription to a specific product.
 * <p>
 * This class tracks the following parameters:
 * <ul>
 * <li><b>id:</b> The unique database identifier for the subscription.</li>
 * <li><b>user:</b> The {@link Userx} who owns this subscription.</li>
 * <li><b>product:</b> The {@link Product} the user is subscribed to.</li>
 * <li><b>types:</b> A set of {@link SubscriptionType} defining what events trigger notifications (e.g., price drops).</li>
 * <li><b>channels:</b> A set of {@link NotificationType} defining how the user is notified (e.g., EMAIL, SMS).</li>
 * </ul>
 */
@Entity
@Table(name = "subscription",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "product_id"})},
        indexes = {@Index(name = "idx_product_subscription", columnList = "product_id")}
)
public class Subscription implements Persistable<Long>, Serializable, PayloadInterface {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Userx user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // Storing the subscription categories as a collection of strings in a separate table
    @ElementCollection(targetClass = SubscriptionType.class, fetch = FetchType.LAZY)
    @CollectionTable(name = "Subscription_Types")
    @Enumerated(EnumType.STRING)
    private Set<SubscriptionType> types;

    // Storing the notification delivery methods as a collection of strings in a separate table
    @ElementCollection(targetClass = NotificationType.class, fetch = FetchType.LAZY)
    @CollectionTable(name = "Notification_Types")
    @Enumerated(EnumType.STRING)
    private Set<NotificationType> channels;

    @Nullable
    @Override
    public Long getId() {
        return id;
    }

    public String getPayloadSubjectLine(){
        return " Subscription Update for " + product.getName();
    }

    public Set<NotificationType> getChannels() {
        return channels;
    }

    public Userx getUser() {
        return user;
    }

    public Product getProduct() {
        return product;
    }

    public Set<SubscriptionType> getTypes() {
        return types;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUser(Userx user) {
        this.user = user;
    }

    public void setChannels(Set<NotificationType> channels) {
        this.channels = channels;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setTypes(Set<SubscriptionType> types) {
        this.types = types;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.getId());
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Subscription other)) {
            return false;
        }
        return Objects.equals(this.getId(), other.getId());
    }

    @Override
    public boolean isNew() {
        // If the ID is null, the entity is considered new for Spring Data's Persistable
        return (null == id);
    }

    @Override
    public String toString() {
        return "at.qe.skeleton.model.Subscription[ id=" + id + " ]";
    }
}