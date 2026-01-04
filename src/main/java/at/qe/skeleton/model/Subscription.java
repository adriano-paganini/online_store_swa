package at.qe.skeleton.model;

import jakarta.persistence.*;
import org.springframework.data.domain.Persistable;
import org.springframework.lang.Nullable;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "subscription",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "product_id"})},
        indexes = {@Index(name = "idx_product_subscription", columnList = "product_id")}
)
public class Subscription implements Persistable<Long>, Serializable {
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

    @ElementCollection(targetClass = SubscriptionType.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "Subscription_Types")
    @Enumerated(EnumType.STRING)
    private Set<SubscriptionType> types;

    @Nullable
    @Override
    public Long getId() {
        return id;
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
        return (null == id);
    }

    @Override
    public String toString() {
        return "at.qe.skeleton.model.Subscription[ id=" + id + " ]";
    }

}
