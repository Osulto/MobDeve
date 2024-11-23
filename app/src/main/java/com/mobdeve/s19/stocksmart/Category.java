package com.mobdeve.s19.stocksmart;

public class Category {
    private long id;
    private String name;
    private int iconResource;
    private String createdAt;
    private String updatedAt;

    // Default constructor
    public Category() {}

    // Constructor without ID (for creation)
    public Category(String name, int iconResource) {
        this.name = name;
        this.iconResource = iconResource;
    }

    // Full constructor
    public Category(long id, String name, int iconResource, String createdAt, String updatedAt) {
        this.id = id;
        this.name = name;
        this.iconResource = iconResource;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIconResource() {
        return iconResource;
    }

    public void setIconResource(int iconResource) {
        this.iconResource = iconResource;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}