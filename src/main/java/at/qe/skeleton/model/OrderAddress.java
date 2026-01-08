package at.qe.skeleton.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serial;
import java.io.Serializable;

/**
 * snapshot of an address for an order, which is embedded inside the order
 * OrderAddress is immutable (no setters)
 * this way it cannot be modified by the user after order creation
 */

@Embeddable
public class OrderAddress implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(nullable = false, length = 100)
    private String country;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(nullable = false, length = 20)
    private String postalCode;

    @Column(nullable = false, length = 150)
    private String street;

    @Column(nullable = false, length = 20)
    private String number;

    @Column(nullable = true, length = 255)
    private String extra;

    protected OrderAddress() {}

    public OrderAddress(String country, String city, String postalCode, String street, String number, String extra) {
        this.country = country;
        this.city = city;
        this.postalCode = postalCode;
        this.street = street;
        this.number = number;
        this.extra = extra;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getStreet() {
        return street;
    }

    public String getNumber() {
        return number;
    }

    public String getExtra() {
        return extra;
    }

    @Override
    public String toString() {
        return "OrderAddress[" +
                ", country='" + country + '\'' +
                ", city='" + city + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", street='" + street + '\'' +
                ", number='" + number + '\'' +
                ", extra='" + extra + '\'' +
                ']';
    }
}
