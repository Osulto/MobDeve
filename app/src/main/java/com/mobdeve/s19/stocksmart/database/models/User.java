package com.mobdeve.s19.stocksmart.database.models;

public class User {
    private long id;
    private String businessName;
    private String username;
    private String password;
    private String createdAt;
    private String updatedAt;

    public User() {}

    public User(String businessName, String username, String password) {
        this.businessName = businessName;
        this.username = username;
        this.password = password;
    }

    public User(long id, String businessName, String username, String password,
                String createdAt, String updatedAt) {
        this.id = id;
        this.businessName = businessName;
        this.username = username;
        this.password = password;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getBusinessName() { return businessName; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}