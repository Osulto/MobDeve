package com.mobdeve.s19.mobdev;

public class Product {

    private String name;
    private int stockCount;
    private String date;

    public Product(String name, int stockCount, String date, String imageUrl) {
        this.name = name;
        this.stockCount = stockCount;
        this.date = date;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public int getStockCount() {
        return stockCount;
    }

    public String getDate() {
        return date;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setStockCount(int stockCount) {
        this.stockCount = stockCount;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
