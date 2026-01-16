package at.qe.skeleton.Helpers;

import at.qe.skeleton.model.Order;
import at.qe.skeleton.model.OrderAddress;
import at.qe.skeleton.model.OrderItem;
import at.qe.skeleton.model.Userx;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

//AI helped with Text generation here
public final class OrderEmailComposer {

    private static final Locale LOCALE_AT = Locale.forLanguageTag("de-AT");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private OrderEmailComposer() {}

    public static String composePlainText(Order order) {
        Objects.requireNonNull(order, "order must not be null");

        Userx user = order.getUser();
        String firstName = safe(user != null ? user.getFirstName() : null, "there");
        String lastName = safe(user != null ? user.getLastName() : null, "");
        String fullName = (firstName + " " + lastName).trim();

        String orderNumber = safe(order.getOrderNumber(), "(unknown)");
        String orderDate = order.getTimestamp() != null ? order.getTimestamp().format(DATE_FMT) : "(unknown)";
        String status = order.getStatus() != null ? order.getStatus().name() : "(unknown)";

        NumberFormat money = NumberFormat.getCurrencyInstance(LOCALE_AT);

        return "Hi " + fullName + ",\n\n" +
                "thanks for your order — we’ve received it and it’s now being processed.\n\n" +
                "Order details\n" +
                "- Order number: " + orderNumber + "\n" +
                "- Order date: " + orderDate + "\n" +
                "- Status: " + status + "\n\n" +
                "Items\n" +
                formatItems(order, money) + "\n" +
                "Order total: " + money.format(nullSafeDouble(order.getTotal())) + "\n\n" +
                "Shipping address\n" +
                formatAddressBlock(order.getShippingAddress()) + "\n\n" +
                "Billing address\n" +
                formatAddressBlock(order.getBillingAddress()) + "\n\n" +
                "What happens next?\n" +
                "- We’ll start preparing your items for shipment.\n" +
                "- As soon as your package is on its way, you’ll receive another update from us.\n\n" +
                "If you have any questions, just reply to this email and include your order number (" +
                orderNumber +
                ") so we can help quickly.\n\n" +
                "Thanks again,\n" +
                "Your Shop Team\n";
    }

    private static String formatItems(Order order, NumberFormat money) {
        if (order.getItems() == null || order.getItems().isEmpty()) {
            return "- (no items)\n";
        }

        StringBuilder sb = new StringBuilder();

        for (OrderItem item : order.getItems()) {
            String name = safe(item.getProductName(), "Item");
            int qty = item.getQuantity() != null ? item.getQuantity() : 0;

            PriceBreakdown p = calculatePrices(item, qty);

            sb.append("- ")
                    .append(qty).append("× ").append(name)
                    .append(" — ").append(money.format(p.lineTotal()))
                    .append(" (unit ").append(money.format(p.unitFinal()));

            if (p.discountPercent() > 0.0) {
                sb.append(" - ").append(trimZeros(p.discountPercent()*100)).append("% off");
            }

            sb.append(")\n");
        }

        return sb.toString();
    }

    private static PriceBreakdown calculatePrices(OrderItem item, int qty) {
        double baseUnit = nullSafeDouble(item.getPriceAtPurchase());

        double discountPercent = item.getAppliedDiscount() != null ? item.getAppliedDiscount() : 0.0;

        double unitFinal = baseUnit * (1.0 - discountPercent / 100.0);
        unitFinal = Math.max(0.0, unitFinal);

        double lineTotal = unitFinal * Math.max(0, qty);

        return new PriceBreakdown(baseUnit, discountPercent, unitFinal, lineTotal);
    }

    private record PriceBreakdown(double baseUnit, double discountPercent, double unitFinal, double lineTotal) {}

    private static String formatAddressBlock(OrderAddress a) {
        if (a == null) return "(not provided)";

        StringBuilder sb = new StringBuilder();

        String street = safe(a.getStreet(), "");
        String number = safe(a.getNumber(), "");
        String line1 = (street + " " + number).trim();
        if (!line1.isBlank()) sb.append(line1).append("\n");

        String postal = safe(a.getPostalCode(), "");
        String city = safe(a.getCity(), "");
        String line2 = (postal + " " + city).trim();
        if (!line2.isBlank()) sb.append(line2).append("\n");

        String country = safe(a.getCountry(), "");
        if (!country.isBlank()) sb.append(country).append("\n");

        String extra = safe(a.getExtra(), "");
        if (!extra.isBlank()) sb.append(extra).append("\n");

        String out = sb.toString().trim();
        return out.isBlank() ? "(not provided)" : out;
    }

    private static String safe(String s, String fallback) {
        if (s == null) return fallback;
        String t = s.trim();
        return t.isEmpty() ? fallback : t;
    }

    private static double nullSafeDouble(Double d) {
        return d != null ? d : 0.0;
    }

    private static String trimZeros(double v) {
        if (v == (long) v) return Long.toString((long) v);
        return Double.toString(v);
    }
}
