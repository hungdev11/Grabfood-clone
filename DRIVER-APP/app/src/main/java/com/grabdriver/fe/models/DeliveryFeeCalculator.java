package com.grabdriver.fe.models;

public class DeliveryFeeCalculator {
    
    // Delivery fee structure based on distance
    private static final long FEE_0_1KM = 11000; // 0-1km: 11,000 VND
    private static final long FEE_1_5KM = 25000; // 1-5km: 25,000 VND
    private static final long FEE_5_10KM = 35000; // 5-10km: 35,000 VND
    private static final long FEE_10_15KM = 45000; // 10-15km: 45,000 VND
    private static final long FEE_ABOVE_15KM = 55000; // >15km: 55,000 VND
    
    // Peak hour multiplier
    private static final double PEAK_HOUR_MULTIPLIER = 1.2; // 20% increase during peak hours
    
    // Holiday/weekend multiplier
    private static final double HOLIDAY_MULTIPLIER = 1.3; // 30% increase during holidays
    
    /**
     * Calculate delivery fee based on distance
     */
    public static long calculateBaseFee(float distance) {
        if (distance <= 1.0f) {
            return FEE_0_1KM;
        } else if (distance <= 5.0f) {
            return FEE_1_5KM;
        } else if (distance <= 10.0f) {
            return FEE_5_10KM;
        } else if (distance <= 15.0f) {
            return FEE_10_15KM;
        } else {
            return FEE_ABOVE_15KM;
        }
    }
    
    /**
     * Calculate delivery fee with peak hour consideration
     */
    public static long calculateFeeWithPeakHour(float distance, boolean isPeakHour) {
        long baseFee = calculateBaseFee(distance);
        if (isPeakHour) {
            return Math.round(baseFee * PEAK_HOUR_MULTIPLIER);
        }
        return baseFee;
    }
    
    /**
     * Calculate delivery fee with holiday consideration
     */
    public static long calculateFeeWithHoliday(float distance, boolean isHoliday) {
        long baseFee = calculateBaseFee(distance);
        if (isHoliday) {
            return Math.round(baseFee * HOLIDAY_MULTIPLIER);
        }
        return baseFee;
    }
    
    /**
     * Calculate full delivery fee with all multipliers
     */
    public static long calculateFullFee(float distance, boolean isPeakHour, boolean isHoliday) {
        long baseFee = calculateBaseFee(distance);
        double multiplier = 1.0;
        
        if (isPeakHour) {
            multiplier *= PEAK_HOUR_MULTIPLIER;
        }
        
        if (isHoliday) {
            multiplier *= HOLIDAY_MULTIPLIER;
        }
        
        return Math.round(baseFee * multiplier);
    }
    
    /**
     * Calculate net earning after commission
     */
    public static long calculateNetEarning(long deliveryFee, long tip) {
        long commission = Math.round(deliveryFee * Wallet.COMMISSION_RATE);
        long netDeliveryFee = deliveryFee - commission;
        return netDeliveryFee + tip;
    }
    
    /**
     * Calculate commission amount
     */
    public static long calculateCommission(long deliveryFee) {
        return Math.round(deliveryFee * Wallet.COMMISSION_RATE);
    }
    
    /**
     * Get distance range description
     */
    public static String getDistanceRangeDescription(float distance) {
        if (distance <= 1.0f) {
            return "0-1km";
        } else if (distance <= 5.0f) {
            return "1-5km";
        } else if (distance <= 10.0f) {
            return "5-10km";
        } else if (distance <= 15.0f) {
            return "10-15km";
        } else {
            return ">15km";
        }
    }
    
    /**
     * Get fee breakdown for display
     */
    public static FeeBreakdown getFeeBreakdown(float distance, long tip, boolean isPeakHour, boolean isHoliday) {
        long baseFee = calculateBaseFee(distance);
        long finalFee = calculateFullFee(distance, isPeakHour, isHoliday);
        long commission = calculateCommission(finalFee);
        long netDeliveryFee = finalFee - commission;
        long totalNet = netDeliveryFee + tip;
        
        return new FeeBreakdown(baseFee, finalFee, commission, netDeliveryFee, tip, totalNet, 
                               isPeakHour, isHoliday, distance);
    }
    
    /**
     * Inner class for fee breakdown
     */
    public static class FeeBreakdown {
        public final long baseFee;
        public final long finalFee;
        public final long commission;
        public final long netDeliveryFee;
        public final long tip;
        public final long totalNet;
        public final boolean isPeakHour;
        public final boolean isHoliday;
        public final float distance;
        
        public FeeBreakdown(long baseFee, long finalFee, long commission, long netDeliveryFee, 
                           long tip, long totalNet, boolean isPeakHour, boolean isHoliday, float distance) {
            this.baseFee = baseFee;
            this.finalFee = finalFee;
            this.commission = commission;
            this.netDeliveryFee = netDeliveryFee;
            this.tip = tip;
            this.totalNet = totalNet;
            this.isPeakHour = isPeakHour;
            this.isHoliday = isHoliday;
            this.distance = distance;
        }
        
        public String getFormattedBaseFee() {
            return String.format("%,d đ", baseFee);
        }
        
        public String getFormattedFinalFee() {
            return String.format("%,d đ", finalFee);
        }
        
        public String getFormattedCommission() {
            return String.format("-%,d đ", commission);
        }
        
        public String getFormattedNetDeliveryFee() {
            return String.format("%,d đ", netDeliveryFee);
        }
        
        public String getFormattedTip() {
            return tip > 0 ? String.format("+%,d đ", tip) : "0 đ";
        }
        
        public String getFormattedTotalNet() {
            return String.format("%,d đ", totalNet);
        }
        
        public String getDistanceRange() {
            return getDistanceRangeDescription(distance);
        }
    }
} 