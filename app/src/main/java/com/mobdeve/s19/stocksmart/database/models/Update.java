package com.mobdeve.s19.stocksmart.database.models;

public class Update {
    private long id;
    private long productId;
    private String productName;  // Transient field, not stored in DB
    private String description;  // Generated field
    private String date;
    private UpdateType type;
    private int quantityChanged;

    public enum UpdateType {
        STOCK_ADDED,
        STOCK_REMOVED,
        STOCK_ADJUSTED,
        PRODUCT_ADDED,
        PRODUCT_REMOVED,
        LOW_STOCK_ALERT,
        PRICE_UPDATED,
        REORDER_POINT_UPDATED
    }

    // Constructor for creating new updates
    public Update(long productId, UpdateType type, int quantityChanged, String date) {
        this.productId = productId;
        this.type = type;
        this.quantityChanged = quantityChanged;
        this.date = date;
        this.description = generateDescription();
    }

    // Constructor for database retrieval
    public Update(long id, long productId, UpdateType type, int quantityChanged, String date) {
        this.id = id;
        this.productId = productId;
        this.type = type;
        this.quantityChanged = quantityChanged;
        this.date = date;
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
            case PRICE_UPDATED:
                return String.format("Price updated to ₱%d", quantityChanged);
            case REORDER_POINT_UPDATED:
                return String.format("Reorder point updated to %d units", quantityChanged);
            default:
                return "Stock updated";
        }
    }

    // Getters and setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getProductId() { return productId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getDate() { return date; }
    public String getTimestamp() { return date; }
    public UpdateType getType() { return type; }
    public int getQuantityChanged() { return quantityChanged; }
}