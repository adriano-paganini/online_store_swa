package at.qe.skeleton.model;

public enum ShippingMethod {
    FAIRY_DUST_DISPATCH("Fairy Dust Dispatch", "✨ Probably enchanted."),
    CARRIER_PIGEON("Carrier Pigeon", "May stop for snacks. Or get lost."),
    WELL_FIGURE_IT_OUT("We'll Figure It Out", "No further details.");

    private final String displayName;
    private final String description;

    ShippingMethod(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
