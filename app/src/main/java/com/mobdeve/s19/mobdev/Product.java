package com.mobdeve.s19.mobdev;

public class Product {
    private long id;
    private String name;
    private String category;
    private int stockCount;
    private String date;
    private int reorderPoint;

    public Product(long id, String name, String category, int stockCount, String date, int reorderPoint) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.stockCount = stockCount;
        this.date = date;
        this.reorderPoint = reorderPoint;
    }

    public long getId() {
        return id;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getStockCount() {
        return stockCount;
    }

    public void setStockCount(int stockCount) {
        this.stockCount = stockCount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getReorderPoint() {
        return reorderPoint;
    }

    public void setReorderPoint(int reorderPoint) {
        this.reorderPoint = reorderPoint;
    }
}