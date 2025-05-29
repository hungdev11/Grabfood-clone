package com.grabdriver.fe.models;

public class OrderItem {
    private Long id;
    private String foodName;
    private int quantity;
    private double price;
    private String note;
    private String imageUrl;

    // Constructors
    public OrderItem() {}

    public OrderItem(String foodName, int quantity, double price) {
        this.foodName = foodName;
        this.quantity = quantity;
        this.price = price;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFoodName() { return foodName; }
    public void setFoodName(String foodName) { this.foodName = foodName; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    // Helper methods
    public double getTotalPrice() {
        return quantity * price;
    }

    public String getFormattedPrice() {
        return String.format("%,.0f đ", price);
    }

    public String getFormattedTotalPrice() {
        return String.format("%,.0f đ", getTotalPrice());
    }
} 