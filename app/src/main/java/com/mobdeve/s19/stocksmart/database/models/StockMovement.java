package com.mobdeve.s19.stocksmart.database.models;

public class StockMovement {
    private long id;
    private long productId;
    private String movementType;
    private int quantity;
    private String supplier;
    private String notes;
    private String createdAt;

    public StockMovement() {}

    public StockMovement(long productId, String movementType, int quantity,
                         String supplier, String notes) {
        this.productId = productId;
        this.movementType = movementType;
        this.quantity = quantity;
        this.supplier = supplier;
        this.notes = notes;
    }

    public StockMovement(long id, long productId, String movementType, int quantity,
                         String supplier, String notes, String createdAt) {
        this.id = id;
        this.productId = productId;
        this.movementType = movementType;
        this.quantity = quantity;
        this.supplier = supplier;
        this.notes = notes;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public String getMovementType() {
        return movementType;
    }

    public void setMovementType(String movementType) {
        this.movementType = movementType;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}