package com.mobdeve.s19.stocksmart;

public class Product {
    private int id;
    private String name;
    private String category;
    private int quantity;
    private String imageUrl;
    private int reorderPoint;
    private double price;
    private String dateAdded;

    public Product(int id, String name, String category, int quantity, String imageUrl,
                   int reorderPoint, double price, String dateAdded) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
        this.reorderPoint = reorderPoint;
        this.price = price;
        this.dateAdded = dateAdded;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public int getReorderPoint() { return reorderPoint; }
    public void setReorderPoint(int reorderPoint) { this.reorderPoint = reorderPoint; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getDateAdded() { return dateAdded; }
    public void setDateAdded(String dateAdded) { this.dateAdded = dateAdded; }

    public boolean isLowStock() {
        return quantity <= reorderPoint;
    }
}