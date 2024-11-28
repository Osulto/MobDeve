package com.mobdeve.s19.stocksmart.database.models;

public class Supplier {
    private long id;
    private long businessId;
    private String name;
    private String contact;
    private String createdAt;
    private String updatedAt;

    // Default constructor
    public Supplier() {}

    // Constructor for creating new supplier
    public Supplier(String name, String contact) {
        this.name = name;
        this.contact = contact;
    }

    // Full constructor
    public Supplier(long id, long businessId, String name, String contact,
                    String createdAt, String updatedAt) {
        this.id = id;
        this.businessId = businessId;
        this.name = name;
        this.contact = contact;
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

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
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