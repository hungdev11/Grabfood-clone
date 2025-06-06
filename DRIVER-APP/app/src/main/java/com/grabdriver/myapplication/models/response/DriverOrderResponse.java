package com.grabdriver.myapplication.models;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class DriverOrderResponse {
    
    @SerializedName("orderId")
    private Long orderId;
    
    @SerializedName("orderStatus")
    private String orderStatus;
    
    @SerializedName("orderDate")
    private Date orderDate;
    
    @SerializedName("customerName")
    private String customerName;
    
    @SerializedName("customerPhone")
    private String customerPhone;
    
    @SerializedName("pickupAddress")
    private String pickupAddress;
    
    @SerializedName("deliveryAddress")
    private String deliveryAddress;
    
    @SerializedName("pickupLatitude")
    private Double pickupLatitude;
    
    @SerializedName("pickupLongitude")
    private Double pickupLongitude;
    
    @SerializedName("deliveryLatitude")
    private Double deliveryLatitude;
    
    @SerializedName("deliveryLongitude")
    private Double deliveryLongitude;
    
    @SerializedName("totalPrice")
    private BigDecimal totalPrice;
    
    @SerializedName("shippingFee")
    private BigDecimal shippingFee;
    
    @SerializedName("deliveryDistance")
    private BigDecimal deliveryDistance;
    
    @SerializedName("estimatedTime")
    private Integer estimatedTime;
    
    @SerializedName("note")
    private String note;
    
    @SerializedName("paymentMethod")
    private String paymentMethod;
    
    @SerializedName("assignedAt")
    private Date assignedAt;
    
    @SerializedName("acceptedAt")
    private Date acceptedAt;
    
    @SerializedName("pickedUpAt")
    private Date pickedUpAt;
    
    @SerializedName("deliveredAt")
    private Date deliveredAt;
    
    @SerializedName("tip")
    private Long tip;
    
    @SerializedName("gemsEarned")
    private Integer gemsEarned;
    
    @SerializedName("shipperEarning")
    private BigDecimal shipperEarning;
    
    @SerializedName("restaurantName")
    private String restaurantName;
    
    @SerializedName("restaurantPhone")
    private String restaurantPhone;
    
    @SerializedName("restaurantAddress")
    private String restaurantAddress;
    
    @SerializedName("items")
    private List<OrderItemResponse> items;

    // Constructors
    public DriverOrderResponse() {
    }

    // Convert to Order model for backward compatibility
    public Order toOrder() {
        Order order = new Order();
        order.setId(orderId != null ? orderId : 0);
        order.setAddress(deliveryAddress != null ? deliveryAddress : "Chưa có địa chỉ");
        order.setNote(note);
        order.setOrderDate(orderDate);
        order.setShippingFee(shippingFee != null ? shippingFee : BigDecimal.ZERO);
        order.setStatus(orderStatus);
        order.setTotalPrice(totalPrice != null ? totalPrice : BigDecimal.ZERO);
        order.setDeliveryDistance(deliveryDistance);
        order.setEstimatedTime(estimatedTime);
        order.setPaymentMethod(paymentMethod);
        order.setTip(tip);
        order.setShipperEarning(shipperEarning);
        order.setCustomerName(customerName);
        order.setCustomerPhone(customerPhone);
        order.setRestaurantName(restaurantName);
        order.setRestaurantAddress(restaurantAddress);
        order.setDeliveryLatitude(deliveryLatitude);
        order.setDeliveryLongitude(deliveryLongitude);
        order.setRestaurantLatitude(pickupLatitude);
        order.setRestaurantLongitude(pickupLongitude);
        order.setAcceptedAt(acceptedAt);
        order.setAssignedAt(assignedAt);
        order.setPickedUpAt(pickedUpAt);
        order.setDeliveredAt(deliveredAt);
        order.setGemsEarned(gemsEarned);
        return order;
    }

    // Getters and Setters
    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getPickupAddress() {
        return pickupAddress;
    }

    public void setPickupAddress(String pickupAddress) {
        this.pickupAddress = pickupAddress;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public Double getPickupLatitude() {
        return pickupLatitude;
    }

    public void setPickupLatitude(Double pickupLatitude) {
        this.pickupLatitude = pickupLatitude;
    }

    public Double getPickupLongitude() {
        return pickupLongitude;
    }

    public void setPickupLongitude(Double pickupLongitude) {
        this.pickupLongitude = pickupLongitude;
    }

    public Double getDeliveryLatitude() {
        return deliveryLatitude;
    }

    public void setDeliveryLatitude(Double deliveryLatitude) {
        this.deliveryLatitude = deliveryLatitude;
    }

    public Double getDeliveryLongitude() {
        return deliveryLongitude;
    }

    public void setDeliveryLongitude(Double deliveryLongitude) {
        this.deliveryLongitude = deliveryLongitude;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public BigDecimal getShippingFee() {
        return shippingFee;
    }

    public void setShippingFee(BigDecimal shippingFee) {
        this.shippingFee = shippingFee;
    }

    public BigDecimal getDeliveryDistance() {
        return deliveryDistance;
    }

    public void setDeliveryDistance(BigDecimal deliveryDistance) {
        this.deliveryDistance = deliveryDistance;
    }

    public Integer getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(Integer estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Date getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(Date assignedAt) {
        this.assignedAt = assignedAt;
    }

    public Date getAcceptedAt() {
        return acceptedAt;
    }

    public void setAcceptedAt(Date acceptedAt) {
        this.acceptedAt = acceptedAt;
    }

    public Date getPickedUpAt() {
        return pickedUpAt;
    }

    public void setPickedUpAt(Date pickedUpAt) {
        this.pickedUpAt = pickedUpAt;
    }

    public Date getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(Date deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public Long getTip() {
        return tip;
    }

    public void setTip(Long tip) {
        this.tip = tip;
    }

    public Integer getGemsEarned() {
        return gemsEarned;
    }

    public void setGemsEarned(Integer gemsEarned) {
        this.gemsEarned = gemsEarned;
    }

    public BigDecimal getShipperEarning() {
        return shipperEarning;
    }

    public void setShipperEarning(BigDecimal shipperEarning) {
        this.shipperEarning = shipperEarning;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getRestaurantPhone() {
        return restaurantPhone;
    }

    public void setRestaurantPhone(String restaurantPhone) {
        this.restaurantPhone = restaurantPhone;
    }

    public String getRestaurantAddress() {
        return restaurantAddress;
    }

    public void setRestaurantAddress(String restaurantAddress) {
        this.restaurantAddress = restaurantAddress;
    }

    public List<OrderItemResponse> getItems() {
        return items;
    }

    public void setItems(List<OrderItemResponse> items) {
        this.items = items;
    }

    // Inner class for order items
    public static class OrderItemResponse {
        @SerializedName("foodName")
        private String foodName;
        
        @SerializedName("quantity")
        private Integer quantity;
        
        @SerializedName("price")
        private BigDecimal price;
        
        @SerializedName("note")
        private String note;
        
        @SerializedName("additions")
        private List<String> additions;

        // Getters and Setters
        public String getFoodName() {
            return foodName;
        }

        public void setFoodName(String foodName) {
            this.foodName = foodName;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }

        public List<String> getAdditions() {
            return additions;
        }

        public void setAdditions(List<String> additions) {
            this.additions = additions;
        }
    }
} 