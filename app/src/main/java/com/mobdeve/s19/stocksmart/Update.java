package com.mobdeve.s19.stocksmart;

public class Update {
    public enum UpdateType {
        STOCK_ADDED,
        STOCK_REMOVED,
        STOCK_ADJUSTED,
        PRODUCT_ADDED,
        PRODUCT_REMOVED,
        LOW_STOCK_ALERT
    }

    private String productName;
    private String description;
    private String date;
    private UpdateType type;
    private int quantityChanged;  // Can be positive or negative

    public Update(String productName, UpdateType type, int quantityChanged, String date) {
        this.productName = productName;
        this.type = type;
        this.quantityChanged = quantityChanged;
        this.date = date;

        // Generate description based on type and quantity
        this.description = generateDescription();
    }

    private String generateDescription() {
        switch (type) {
            case STOCK_ADDED:
                return String.format("Added %d units to stock", quantityChanged);
            case STOCK_REMOVED:
                return String.format("Removed %d units from stock", Math.abs(quantityChanged));
            case STOCK_ADJUSTED:
                return String.format("Stock adjusted by %d units", quantityChanged);
            case PRODUCT_ADDED:
                return "New product added to inventory";
            case PRODUCT_REMOVED:
                return "Product removed from inventory";
            case LOW_STOCK_ALERT:
                return String.format("Low stock alert: %d units remaining", quantityChanged);
            default:
                return "Stock updated";
        }
    }

    public String getProductName() {
        return productName;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public UpdateType getType() {
        return type;
    }

    public int getQuantityChanged() {
        return quantityChanged;
    }
}