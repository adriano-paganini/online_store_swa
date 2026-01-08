package at.qe.skeleton.model;

import jakarta.persistence.*;
import org.springframework.data.domain.Persistable;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Entity
public class OrderItem implements Persistable<Long>, Serializable, Comparable<OrderItem> {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // product id at purchase time
    @Column(nullable = false)
    private Long productId;

    // product name at purchase time
    @Column(nullable = false)
    private String productName;

     // Price per unit at the moment of purchase (before discount).
    @Column(nullable = false)
    private Double priceAtPurchase;

    // discount at purchase time
    @Column(nullable = true)
    private Double appliedDiscount;

    @Column(nullable = false)
    private Integer quantity;

    public Long getId() {
        return id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Double getPriceAtPurchase() {
        return priceAtPurchase;
    }

    public void setPriceAtPurchase(Double priceAtPurchase) {
        this.priceAtPurchase = priceAtPurchase;
    }

    public Double getAppliedDiscount() {
        return appliedDiscount;
    }

    public void setAppliedDiscount(Double appliedDiscount) {
        this.appliedDiscount = appliedDiscount;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public boolean isNew() {
        return id == null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof OrderItem other)) {
            return false;
        }
        return Objects.equals(this.getId(), other.getId());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.getId());
        return hash;
    }

    @Override
    public String toString() {
        return "at.qe.skeleton.model.OrderItem[" +
                "id=" + id +
                ", productName='" + productName + ']';
    }

    @Override
    public int compareTo(OrderItem o) {
        return this.id.compareTo(o.getId());
    }
}
