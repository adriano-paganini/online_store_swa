package at.qe.skeleton.model;

import at.qe.skeleton.events.PayloadInterface;
import jakarta.persistence.*;
import org.springframework.data.domain.Persistable;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


@Entity
@Table(name = "orders")
public class Order implements Persistable<Long>, Serializable, PayloadInterface {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String orderNumber;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OrderStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ShippingMethod shippingMethod;

    @Column(nullable = false)
    private Double total;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private Userx user;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "order_id", nullable = false)
    private List<OrderItem> items;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "country", column = @Column(name = "billing_country")),
            @AttributeOverride(name = "city", column = @Column(name = "billing_city")),
            @AttributeOverride(name = "postalCode", column = @Column(name = "billing_postal_code")),
            @AttributeOverride(name = "street", column = @Column(name = "billing_street")),
            @AttributeOverride(name = "number", column = @Column(name = "billing_number")),
            @AttributeOverride(name = "extra", column = @Column(name = "billing_extra"))
    })
    private OrderAddress billingAddress;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "country", column = @Column(name = "shipping_country")),
            @AttributeOverride(name = "city", column = @Column(name = "shipping_city")),
            @AttributeOverride(name = "postalCode", column = @Column(name = "shipping_postal_code")),
            @AttributeOverride(name = "street", column = @Column(name = "shipping_street")),
            @AttributeOverride(name = "number", column = @Column(name = "shipping_number")),
            @AttributeOverride(name = "extra", column = @Column(name = "shipping_extra"))
    })
    private OrderAddress shippingAddress;

    protected Order() {
    }

    public Order(
            Userx user,
            List<OrderItem> items,
            OrderAddress billingAddress,
            OrderAddress shippingAddress,
            ShippingMethod shippingMethod,
            Double total
    ) {
        this.user = user;
        this.items = items;
        this.billingAddress = billingAddress;
        this.shippingAddress = shippingAddress;
        this.shippingMethod = shippingMethod;
        this.total = total;
        this.status = OrderStatus.PENDING;
    }

    @PrePersist
    private void prePersist() {
        if (this.orderNumber == null) {
            this.orderNumber = "ORD-" + UUID.randomUUID()
                    .toString()
                    .substring(0, 8)
                    .toUpperCase();
        }
        if (this.timestamp == null) {
            this.timestamp = LocalDateTime.now();
        }
    }
    @Override
    public Long getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return id == null;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Userx getUser() {
        return user;
    }

    public void setUser(Userx user) {
        this.user = user;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public OrderAddress getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(OrderAddress billingAddress) {
        this.billingAddress = billingAddress;
    }

    public OrderAddress getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(OrderAddress shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public ShippingMethod getShippingMethod() {
        return shippingMethod;
    }

    public void setShippingMethod(ShippingMethod shippingMethod) {
        this.shippingMethod = shippingMethod;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order other)) return false;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String getPayloadSubjectLine() {
        return "Order "+orderNumber+" has been confirmed and will be on it's way!";
    }
}
