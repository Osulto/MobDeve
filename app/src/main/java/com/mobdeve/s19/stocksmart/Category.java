package com.mobdeve.s19.stocksmart;

public class Category {
    private long id;
    private String name;
    private String iconPath;
    private String createdAt;
    private String updatedAt;

    // Default constructor
    public Category() {}

    // Constructor without ID (for creation)
    public Category(String name, String iconPath) {
        this.name = name;
        this.iconPath = iconPath;
    }

    // Full constructor
    public Category(long id, String name, String iconPath, String createdAt, String updatedAt) {
        this.id = id;
        this.name = name;
        this.iconPath = iconPath;
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

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(int iconResource) {
        this.iconPath = iconPath;
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