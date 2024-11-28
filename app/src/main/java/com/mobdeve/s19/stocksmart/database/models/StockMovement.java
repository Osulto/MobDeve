package com.mobdeve.s19.stocksmart.database.models;

public class StockMovement {
    private long id;
    private long businessId;
    private long productId;
    private String movementType;
    private int quantity;
    private long supplierId;  // Changed from String supplier to long supplierId
    private double supplierPrice;  // Added supplier price
    private String notes;
    private String createdAt;

    public StockMovement() {}

    // Constructor for creating new stock movement
    public StockMovement(long productId, String movementType, int quantity,
                         long supplierId, double supplierPrice) {
        this.productId = productId;
        this.movementType = movementType;
        this.quantity = quantity;
        this.supplierId = supplierId;
        this.supplierPrice = supplierPrice;
    }

    // Full constructor
    public StockMovement(long id, long businessId, long productId, String movementType,
                         int quantity, long supplierId, double supplierPrice,
                         String notes, String createdAt) {
        this.id = id;
        this.businessId = businessId;
        this.productId = productId;
        this.movementType = movementType;
        this.quantity = quantity;
        this.supplierId = supplierId;
        this.supplierPrice = supplierPrice;
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

    public long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(long businessId) {
        this.businessId = businessId;
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

    public long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(long supplierId) {
        this.supplierId = supplierId;
    }

    public double getSupplierPrice() {
        return supplierPrice;
    }

    public void setSupplierPrice(double supplierPrice) {
        this.supplierPrice = supplierPrice;
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