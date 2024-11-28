package com.mobdeve.s19.stocksmart.database.models;

public class Product {
    private long id;
    private long businessId;
    private String name;
    private long categoryId;
    private int quantity;
    private int reorderPoint;
    private double supplierPrice;  // Changed from costPrice
    private double sellingPrice;
    private Long supplierId;       // Added (Long to allow null)
    private String qrCode;
    private String description;
    private String createdAt;
    private String updatedAt;

    // Default constructor
    public Product() {}

    // Constructor for creation
    public Product(String name, long categoryId, int quantity, int reorderPoint,
                   double supplierPrice, double sellingPrice, Long supplierId,
                   String qrCode, String description) {
        this.name = name;
        this.categoryId = categoryId;
        this.quantity = quantity;
        this.reorderPoint = reorderPoint;
        this.supplierPrice = supplierPrice;
        this.sellingPrice = sellingPrice;
        this.supplierId = supplierId;
        this.qrCode = qrCode;
        this.description = description;
    }

    // Full constructor
    public Product(long id, long businessId, String name, long categoryId,
                   int quantity, int reorderPoint, double supplierPrice,
                   double sellingPrice, Long supplierId, String qrCode,
                   String description, String createdAt, String updatedAt) {
        this.id = id;
        this.businessId = businessId;
        this.name = name;
        this.categoryId = categoryId;
        this.quantity = quantity;
        this.reorderPoint = reorderPoint;
        this.supplierPrice = supplierPrice;
        this.sellingPrice = sellingPrice;
        this.supplierId = supplierId;
        this.qrCode = qrCode;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Existing getters and setters...
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getBusinessId() { return businessId; }
    public void setBusinessId(long businessId) { this.businessId = businessId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public long getCategoryId() { return categoryId; }
    public void setCategoryId(long categoryId) { this.categoryId = categoryId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public int getReorderPoint() { return reorderPoint; }
    public void setReorderPoint(int reorderPoint) { this.reorderPoint = reorderPoint; }

    // Updated and new getters/setters
    public double getSupplierPrice() { return supplierPrice; }
    public void setSupplierPrice(double supplierPrice) { this.supplierPrice = supplierPrice; }
    public double getSellingPrice() { return sellingPrice; }
    public void setSellingPrice(double sellingPrice) { this.sellingPrice = sellingPrice; }
    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }

    // Remaining existing getters/setters
    public String getQrCode() { return qrCode; }
    public void setQrCode(String qrCode) { this.qrCode = qrCode; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    public boolean isLowStock() {
        return quantity <= reorderPoint;
    }
}