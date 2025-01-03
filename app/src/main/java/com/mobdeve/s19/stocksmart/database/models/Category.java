package com.mobdeve.s19.stocksmart.database.models;

public class Category {
    private long id;
    private long businessId;
    private String name;
    private String iconPath;
    private String createdAt;
    private String updatedAt;

    public Category() {}

    public Category(String name, String iconPath) {
        this.name = name;
        this.iconPath = iconPath;
    }

    public Category(long id, String name, String iconPath, String createdAt, String updatedAt) {
        this.id = id;
        this.name = name;
        this.iconPath = iconPath;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
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