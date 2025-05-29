package com.grabdriver.fe.models;

import java.util.Date;
import java.util.List;

public class Order {
    private String id;
    private String customerName;
    private String customerPhone;
    private String restaurantName;
    private String restaurantAddress;
    private String deliveryAddress;
    private double restaurantLatitude;
    private double restaurantLongitude;
    private double deliveryLatitude;
    private double deliveryLongitude;
    private long totalAmount;
    private long deliveryFee;
    private String status; // PENDING, ACCEPTED, PICKED_UP, DELIVERING, COMPLETED, CANCELLED
    private Date orderTime;
    private String note;
    private String paymentMethod; // COD, MOMO, etc.
    private List<OrderItem> items;
    private int gemsEarned;
    private float distance;
    private int estimatedTime; // in minutes
    private long tip; // Tip từ khách hàng
    private String paymentType; // "ONLINE" hoặc "COD"

    // Constructors
    public Order() {}

    public Order(String id, String customerName, String customerPhone, 
                String restaurantName, String deliveryAddress, long totalAmount) {
        this.id = id;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.restaurantName = restaurantName;
        this.deliveryAddress = deliveryAddress;
        this.totalAmount = totalAmount;
        this.status = "pending";
        this.orderTime = new Date();
        this.gemsEarned = calculateGems();
        this.tip = 0;
        this.paymentType = "ONLINE";
    }

    // Calculate gems based on order value
    private int calculateGems() {
        int baseGems = 20; // Base gems per order
        int bonusGems = (totalAmount >= 300000) ? 2 : 0; // Bonus for orders >= 300k
        return baseGems + bonusGems;
    }
    
    // Calculate net earning after 15% commission
    public long calculateNetEarning() {
        long commission = Math.round(deliveryFee * Wallet.COMMISSION_RATE);
        long netDeliveryFee = deliveryFee - commission;
        return netDeliveryFee + tip;
    }
    
    // Calculate commission amount
    public long getCommissionAmount() {
        return Math.round(deliveryFee * Wallet.COMMISSION_RATE);
    }
    
    // Get earning breakdown for display
    public String getEarningBreakdown() {
        long commission = getCommissionAmount();
        long netEarning = calculateNetEarning();
        
        StringBuilder breakdown = new StringBuilder();
        breakdown.append("Phí giao: ").append(getFormattedShippingFee());
        breakdown.append("\nChiết khấu: -").append(String.format("%,d đ", commission));
        if (tip > 0) {
            breakdown.append("\nTip: +").append(String.format("%,d đ", tip));
        }
        breakdown.append("\nThực nhận: ").append(String.format("%,d đ", netEarning));
        
        return breakdown.toString();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public String getRestaurantName() { return restaurantName; }
    public void setRestaurantName(String restaurantName) { this.restaurantName = restaurantName; }

    public String getRestaurantAddress() { return restaurantAddress; }
    public void setRestaurantAddress(String restaurantAddress) { this.restaurantAddress = restaurantAddress; }

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    public double getRestaurantLatitude() { return restaurantLatitude; }
    public void setRestaurantLatitude(double restaurantLatitude) { this.restaurantLatitude = restaurantLatitude; }

    public double getRestaurantLongitude() { return restaurantLongitude; }
    public void setRestaurantLongitude(double restaurantLongitude) { this.restaurantLongitude = restaurantLongitude; }

    public double getDeliveryLatitude() { return deliveryLatitude; }
    public void setDeliveryLatitude(double deliveryLatitude) { this.deliveryLatitude = deliveryLatitude; }

    public double getDeliveryLongitude() { return deliveryLongitude; }
    public void setDeliveryLongitude(double deliveryLongitude) { this.deliveryLongitude = deliveryLongitude; }

    public long getTotalAmount() { return totalAmount; }
    public void setTotalAmount(long totalAmount) { 
        this.totalAmount = totalAmount;
        this.gemsEarned = calculateGems(); // Recalculate gems when price changes
    }

    public long getDeliveryFee() { return deliveryFee; }
    public void setDeliveryFee(long deliveryFee) { this.deliveryFee = deliveryFee; }

    public double getTotalPrice() { return totalAmount; }
    public void setTotalPrice(double totalPrice) { 
        this.totalAmount = (long) totalPrice;
        this.gemsEarned = calculateGems();
    }

    public double getShippingFee() { return deliveryFee; }
    public void setShippingFee(double shippingFee) { this.deliveryFee = (long) shippingFee; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getOrderTime() { return orderTime; }
    public void setOrderTime(Date orderTime) { this.orderTime = orderTime; }

    public Date getOrderDate() { return orderTime; }
    public void setOrderDate(Date orderDate) { this.orderTime = orderDate; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }

    public int getGemsEarned() { return gemsEarned; }
    public void setGemsEarned(int gemsEarned) { this.gemsEarned = gemsEarned; }

    public float getDistance() { return distance; }
    public void setDistance(float distance) { this.distance = distance; }

    public int getEstimatedTime() { return estimatedTime; }
    public void setEstimatedTime(int estimatedTime) { this.estimatedTime = estimatedTime; }

    public long getTip() { return tip; }
    public void setTip(long tip) { this.tip = tip; }
    
    public String getPaymentType() { return paymentType; }
    public void setPaymentType(String paymentType) { this.paymentType = paymentType; }

    // Helper methods
    public String getFormattedPrice() {
        return String.format("%,.0f đ", (double) totalAmount);
    }

    public String getFormattedShippingFee() {
        return String.format("%,.0f đ", (double) deliveryFee);
    }
    
    public String getFormattedNetEarning() {
        return String.format("%,d đ", calculateNetEarning());
    }
    
    public String getFormattedTip() {
        return tip > 0 ? String.format("+%,d đ", tip) : "";
    }
    
    public String getPaymentTypeDisplay() {
        return "COD".equals(paymentType) ? "Tiền mặt" : "Online";
    }

    public boolean isHighValueOrder() {
        return totalAmount >= 300000;
    }
}