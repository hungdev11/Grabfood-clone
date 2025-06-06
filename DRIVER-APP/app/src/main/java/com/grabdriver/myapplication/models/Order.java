package com.grabdriver.myapplication.models;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.util.Date;

public class Order implements Parcelable {
    @SerializedName("id")
    private long id;
    
    @SerializedName("address")
    private String address;
    
    @SerializedName("note")
    private String note;
    
    @SerializedName("orderDate")
    private Date orderDate;
    
    @SerializedName("shippingFee")
    private BigDecimal shippingFee;
    
    @SerializedName("status")
    private String status; // PENDING, CANCELLED, PROCESSING, READY_FOR_PICKUP, SHIPPING, COMPLETED, REJECTED
    
    @SerializedName("totalPrice")
    private BigDecimal totalPrice;
    
    @SerializedName("userId")
    private long userId;
    
    @SerializedName("discountOrderPrice")
    private BigDecimal discountOrderPrice;
    
    @SerializedName("discountShippingFee")
    private BigDecimal discountShippingFee;
    
    @SerializedName("acceptedAt")
    private Date acceptedAt;
    
    @SerializedName("assignedAt")
    private Date assignedAt;
    
    @SerializedName("deliveredAt")
    private Date deliveredAt;
    
    @SerializedName("deliveryLatitude")
    private Double deliveryLatitude;
    
    @SerializedName("deliveryLongitude")
    private Double deliveryLongitude;
    
    @SerializedName("distance")
    private Float distance;
    
    @SerializedName("estimatedTime")
    private Integer estimatedTime;
    
    @SerializedName("gemsEarned")
    private Integer gemsEarned;
    
    @SerializedName("pickedUpAt")
    private Date pickedUpAt;
    
    @SerializedName("tip")
    private Long tip;
    
    @SerializedName("shipperId")
    private Long shipperId;
    
    @SerializedName("deliveryDistance")
    private BigDecimal deliveryDistance;
    
    @SerializedName("paymentMethod")
    private String paymentMethod; // COD, VNPAY
    
    @SerializedName("shipperEarning")
    private BigDecimal shipperEarning;

    // Additional fields for enhanced functionality
    @SerializedName("customerName")
    private String customerName;
    
    @SerializedName("customerPhone")
    private String customerPhone;
    
    @SerializedName("restaurantLatitude")
    private Double restaurantLatitude;
    
    @SerializedName("restaurantLongitude")
    private Double restaurantLongitude;
    
    @SerializedName("restaurantName")
    private String restaurantName;
    
    @SerializedName("restaurantAddress")
    private String restaurantAddress;
    
    @SerializedName("isUrgent")
    private boolean isUrgent;
    
    @SerializedName("customerAvatar")
    private String customerAvatar;
    
    @SerializedName("specialInstructions")
    private String specialInstructions;

    // Constructors
    public Order() {
    }

    public Order(long id, String address, Date orderDate, String status,
            BigDecimal totalPrice, BigDecimal shippingFee, String paymentMethod) {
        this.id = id;
        this.address = address;
        this.orderDate = orderDate;
        this.status = status;
        this.totalPrice = totalPrice;
        this.shippingFee = shippingFee;
        this.paymentMethod = paymentMethod;
    }

    // Parcelable implementation
    protected Order(Parcel in) {
        id = in.readLong();
        address = in.readString();
        note = in.readString();
        long tmpOrderDate = in.readLong();
        orderDate = tmpOrderDate != -1 ? new Date(tmpOrderDate) : null;
        shippingFee = (BigDecimal) in.readSerializable();
        status = in.readString();
        totalPrice = (BigDecimal) in.readSerializable();
        userId = in.readLong();
        discountOrderPrice = (BigDecimal) in.readSerializable();
        discountShippingFee = (BigDecimal) in.readSerializable();
        long tmpAcceptedAt = in.readLong();
        acceptedAt = tmpAcceptedAt != -1 ? new Date(tmpAcceptedAt) : null;
        long tmpAssignedAt = in.readLong();
        assignedAt = tmpAssignedAt != -1 ? new Date(tmpAssignedAt) : null;
        long tmpDeliveredAt = in.readLong();
        deliveredAt = tmpDeliveredAt != -1 ? new Date(tmpDeliveredAt) : null;
        deliveryLatitude = (Double) in.readValue(Double.class.getClassLoader());
        deliveryLongitude = (Double) in.readValue(Double.class.getClassLoader());
        distance = (Float) in.readValue(Float.class.getClassLoader());
        estimatedTime = (Integer) in.readValue(Integer.class.getClassLoader());
        gemsEarned = (Integer) in.readValue(Integer.class.getClassLoader());
        long tmpPickedUpAt = in.readLong();
        pickedUpAt = tmpPickedUpAt != -1 ? new Date(tmpPickedUpAt) : null;
        tip = (Long) in.readValue(Long.class.getClassLoader());
        shipperId = (Long) in.readValue(Long.class.getClassLoader());
        deliveryDistance = (BigDecimal) in.readSerializable();
        paymentMethod = in.readString();
        shipperEarning = (BigDecimal) in.readSerializable();
        customerName = in.readString();
        customerPhone = in.readString();
        restaurantLatitude = (Double) in.readValue(Double.class.getClassLoader());
        restaurantLongitude = (Double) in.readValue(Double.class.getClassLoader());
        restaurantName = in.readString();
        restaurantAddress = in.readString();
        isUrgent = in.readByte() != 0;
        customerAvatar = in.readString();
        specialInstructions = in.readString();
    }

    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(address);
        dest.writeString(note);
        dest.writeLong(orderDate != null ? orderDate.getTime() : -1);
        dest.writeSerializable(shippingFee);
        dest.writeString(status);
        dest.writeSerializable(totalPrice);
        dest.writeLong(userId);
        dest.writeSerializable(discountOrderPrice);
        dest.writeSerializable(discountShippingFee);
        dest.writeLong(acceptedAt != null ? acceptedAt.getTime() : -1);
        dest.writeLong(assignedAt != null ? assignedAt.getTime() : -1);
        dest.writeLong(deliveredAt != null ? deliveredAt.getTime() : -1);
        dest.writeValue(deliveryLatitude);
        dest.writeValue(deliveryLongitude);
        dest.writeValue(distance);
        dest.writeValue(estimatedTime);
        dest.writeValue(gemsEarned);
        dest.writeLong(pickedUpAt != null ? pickedUpAt.getTime() : -1);
        dest.writeValue(tip);
        dest.writeValue(shipperId);
        dest.writeSerializable(deliveryDistance);
        dest.writeString(paymentMethod);
        dest.writeSerializable(shipperEarning);
        dest.writeString(customerName);
        dest.writeString(customerPhone);
        dest.writeValue(restaurantLatitude);
        dest.writeValue(restaurantLongitude);
        dest.writeString(restaurantName);
        dest.writeString(restaurantAddress);
        dest.writeByte((byte) (isUrgent ? 1 : 0));
        dest.writeString(customerAvatar);
        dest.writeString(specialInstructions);
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public BigDecimal getShippingFee() {
        return shippingFee;
    }

    public void setShippingFee(BigDecimal shippingFee) {
        this.shippingFee = shippingFee;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public BigDecimal getDiscountOrderPrice() {
        return discountOrderPrice;
    }

    public void setDiscountOrderPrice(BigDecimal discountOrderPrice) {
        this.discountOrderPrice = discountOrderPrice;
    }

    public BigDecimal getDiscountShippingFee() {
        return discountShippingFee;
    }

    public void setDiscountShippingFee(BigDecimal discountShippingFee) {
        this.discountShippingFee = discountShippingFee;
    }

    public Date getAcceptedAt() {
        return acceptedAt;
    }

    public void setAcceptedAt(Date acceptedAt) {
        this.acceptedAt = acceptedAt;
    }

    public Date getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(Date assignedAt) {
        this.assignedAt = assignedAt;
    }

    public Date getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(Date deliveredAt) {
        this.deliveredAt = deliveredAt;
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

    public Float getDistance() {
        return distance;
    }

    public void setDistance(Float distance) {
        this.distance = distance;
    }

    public Integer getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(Integer estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public Integer getGemsEarned() {
        return gemsEarned;
    }

    public void setGemsEarned(Integer gemsEarned) {
        this.gemsEarned = gemsEarned;
    }

    public Date getPickedUpAt() {
        return pickedUpAt;
    }

    public void setPickedUpAt(Date pickedUpAt) {
        this.pickedUpAt = pickedUpAt;
    }

    public Long getTip() {
        return tip;
    }

    public void setTip(Long tip) {
        this.tip = tip;
    }

    public Long getShipperId() {
        return shipperId;
    }

    public void setShipperId(Long shipperId) {
        this.shipperId = shipperId;
    }

    public BigDecimal getDeliveryDistance() {
        return deliveryDistance;
    }

    public void setDeliveryDistance(BigDecimal deliveryDistance) {
        this.deliveryDistance = deliveryDistance;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public BigDecimal getShipperEarning() {
        return shipperEarning;
    }

    public void setShipperEarning(BigDecimal shipperEarning) {
        this.shipperEarning = shipperEarning;
    }

    // New getters and setters
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

    public Double getRestaurantLatitude() {
        return restaurantLatitude;
    }

    public void setRestaurantLatitude(Double restaurantLatitude) {
        this.restaurantLatitude = restaurantLatitude;
    }

    public Double getRestaurantLongitude() {
        return restaurantLongitude;
    }

    public void setRestaurantLongitude(Double restaurantLongitude) {
        this.restaurantLongitude = restaurantLongitude;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getRestaurantAddress() {
        return restaurantAddress;
    }

    public void setRestaurantAddress(String restaurantAddress) {
        this.restaurantAddress = restaurantAddress;
    }

    public boolean isUrgent() {
        return isUrgent;
    }

    public void setUrgent(boolean urgent) {
        isUrgent = urgent;
    }

    public String getCustomerAvatar() {
        return customerAvatar;
    }

    public void setCustomerAvatar(String customerAvatar) {
        this.customerAvatar = customerAvatar;
    }

    public String getSpecialInstructions() {
        return specialInstructions;
    }

    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
    }

    // Utility methods
    public String getFormattedTotalPrice() {
        if (totalPrice != null) {
            return String.format("%,.0f₫", totalPrice.doubleValue());
        }
        return "0₫";
    }

    public String getFormattedShippingFee() {
        if (shippingFee != null) {
            return String.format("%,.0f₫", shippingFee.doubleValue());
        }
        return "0₫";
    }

    public boolean canStartDelivery() {
        return "READY_FOR_PICKUP".equals(status) || "ASSIGNED".equals(status);
    }

    public boolean isInProgress() {
        return "SHIPPING".equals(status) || "PROCESSING".equals(status);
    }

    public boolean isCompleted() {
        return "COMPLETED".equals(status);
    }

    public boolean isCancelled() {
        return "CANCELLED".equals(status) || "REJECTED".equals(status);
    }
}