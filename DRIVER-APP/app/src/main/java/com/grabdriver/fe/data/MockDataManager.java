package com.grabdriver.fe.data;

import com.grabdriver.fe.models.Order;
import com.grabdriver.fe.models.OrderItem;
import com.grabdriver.fe.models.Reward;
import com.grabdriver.fe.models.Shipper;
import com.grabdriver.fe.models.Wallet;
import com.grabdriver.fe.models.Transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MockDataManager {
    
    // Fake login credentials - chỉ dùng số điện thoại
    public static final String DEMO_PHONE = "0901234567";
    public static final String DEMO_PASSWORD = "123456";
    
    // Alternative phone numbers for testing
    public static final String DEMO_PHONE2 = "0987654321";
    public static final String DEMO_PASSWORD2 = "123456";
    
    public static final String DEMO_PHONE3 = "0912345678";
    public static final String DEMO_PASSWORD3 = "123456";
    
    public static boolean validateLogin(String phone, String password) {
        return (DEMO_PHONE.equals(phone) && DEMO_PASSWORD.equals(password)) ||
               (DEMO_PHONE2.equals(phone) && DEMO_PASSWORD2.equals(password)) ||
               (DEMO_PHONE3.equals(phone) && DEMO_PASSWORD3.equals(password));
    }
    
    public static Shipper createMockShipper(String phone) {
        Shipper shipper = new Shipper();
        shipper.setId("SH001");
        shipper.setName("Nguyễn Văn A");
        shipper.setPhone(phone);
        shipper.setEmail("nguyenvana@email.com");
        shipper.setVehicleType("Xe máy");
        shipper.setVehicleNumber("29A1-12345");
        if (DEMO_PHONE.equals(phone)) {
            shipper.setGems(1250);
            shipper.setRating(4.8f);
            shipper.setAcceptanceRate(95.5f);
            shipper.setCancellationRate(2.1f);
            shipper.setTotalEarnings(45680000L);
        } else if (DEMO_PHONE2.equals(phone)) {
            shipper.setGems(890);
            shipper.setRating(4.6f);
            shipper.setAcceptanceRate(88.2f);
            shipper.setCancellationRate(8.5f);
            shipper.setTotalEarnings(32450000L);
        } else if (DEMO_PHONE3.equals(phone)) {
            shipper.setGems(2150);
            shipper.setRating(4.9f);
            shipper.setAcceptanceRate(97.8f);
            shipper.setCancellationRate(1.2f);
            shipper.setTotalEarnings(58920000L);
        } else {
            // Default for new users
            shipper.setGems(500);
            shipper.setRating(5.0f);
            shipper.setAcceptanceRate(100.0f);
            shipper.setCancellationRate(0.0f);
            shipper.setTotalEarnings(5000000L);
        }
        shipper.setTotalOrders(156);
        shipper.setIsOnline(true);
        return shipper;
    }
    
    public static List<Order> createMockOrders() {
        List<Order> orders = new ArrayList<>();
        
        // Order 1 - Pending
        Order order1 = new Order();
        order1.setId("ORD001");
        order1.setCustomerName("Nguyễn Thị Lan");
        order1.setCustomerPhone("0901234567");
        order1.setRestaurantName("Phở Hà Nội");
        order1.setRestaurantAddress("123 Nguyễn Huệ, Q1, TP.HCM");
        order1.setDeliveryAddress("456 Lê Lợi, Q1, TP.HCM");
        order1.setStatus("pending");
        order1.setTotalAmount(350000L);
        order1.setDeliveryFee(25000L);
        order1.setDistance(2.5f);
        order1.setOrderTime(new Date());
        order1.setEstimatedTime(25);
        order1.setGemsEarned(22); // 20 base + 2 bonus for order >= 300k
        order1.setTip(5000L); // Customer tip
        order1.setPaymentType("ONLINE");

        List<OrderItem> items1 = new ArrayList<>();
        items1.add(new OrderItem("Phở bò tái", 2, 85000));
        items1.add(new OrderItem("Chả cá", 1, 45000));
        items1.add(new OrderItem("Nước ngọt", 2, 15000));
        order1.setItems(items1);
        
        orders.add(order1);
        
        // Order 2 - Delivering
        Order order2 = new Order();
        order2.setId("ORD002");
        order2.setCustomerName("Trần Văn Nam");
        order2.setCustomerPhone("0987654321");
        order2.setRestaurantName("Cơm Tấm Sài Gòn");
        order2.setRestaurantAddress("789 Điện Biên Phủ, Q3, TP.HCM");
        order2.setDeliveryAddress("321 Võ Văn Tần, Q3, TP.HCM");
        order2.setStatus("delivering");
        order2.setTotalAmount(180000L);
        order2.setDeliveryFee(20000L);
        order2.setDistance(1.8f);
        order2.setOrderTime(new Date(System.currentTimeMillis() - 900000)); // 15 phút trước
        order2.setEstimatedTime(15);
        order2.setGemsEarned(20); // 20 base gems
        order2.setTip(0L); // No tip
        order2.setPaymentType("COD");

        List<OrderItem> items2 = new ArrayList<>();
        items2.add(new OrderItem("Cơm tấm sườn", 1, 65000));
        items2.add(new OrderItem("Trà đá", 1, 5000));
        order2.setItems(items2);
        
        orders.add(order2);
        
        // Order 3 - Completed
        Order order3 = new Order();
        order3.setId("ORD003");
        order3.setCustomerName("Lê Thị Hoa");
        order3.setCustomerPhone("0912345678");
        order3.setRestaurantName("Bánh Mì Huỳnh Hoa");
        order3.setRestaurantAddress("26 Lê Thị Riêng, Q1, TP.HCM");
        order3.setDeliveryAddress("100 Nguyễn Thị Minh Khai, Q3, TP.HCM");
        order3.setStatus("completed");
        order3.setTotalAmount(120000L);
        order3.setDeliveryFee(30000L);
        order3.setDistance(3.2f);
        order3.setOrderTime(new Date(System.currentTimeMillis() - 3600000)); // 1 giờ trước
        order3.setEstimatedTime(20);
        order3.setGemsEarned(20);
        order3.setTip(10000L); // Good tip
        order3.setPaymentType("ONLINE");

        List<OrderItem> items3 = new ArrayList<>();
        items3.add(new OrderItem("Bánh mì thịt nướng", 3, 35000));
        items3.add(new OrderItem("Nước cam", 1, 15000));
        order3.setItems(items3);
        
        orders.add(order3);
        
        // Order 4 - Pending (High value)
        Order order4 = new Order();
        order4.setId("ORD004");
        order4.setCustomerName("Phạm Văn Đức");
        order4.setCustomerPhone("0923456789");
        order4.setRestaurantName("Lẩu Thái Siêu Cay");
        order4.setRestaurantAddress("45 Nguyễn Trãi, Q5, TP.HCM");
        order4.setDeliveryAddress("67 Trần Hưng Đạo, Q5, TP.HCM");
        order4.setStatus("pending");
        order4.setTotalAmount(580000L);
        order4.setDeliveryFee(35000L);
        order4.setDistance(4.1f);
        order4.setOrderTime(new Date());
        order4.setEstimatedTime(35);
        order4.setGemsEarned(22); // 20 base + 2 bonus
        order4.setTip(15000L); // High value order tip
        order4.setPaymentType("COD");

        List<OrderItem> items4 = new ArrayList<>();
        items4.add(new OrderItem("Lẩu Tomyum", 1, 280000));
        items4.add(new OrderItem("Bánh tráng nướng", 2, 45000));
        items4.add(new OrderItem("Nước dừa", 2, 25000));
        items4.add(new OrderItem("Kem flan", 3, 20000));
        order4.setItems(items4);
        
        orders.add(order4);
        
        // Order 5 - Cancelled
        Order order5 = new Order();
        order5.setId("ORD005");
        order5.setCustomerName("Võ Thị Mai");
        order5.setCustomerPhone("0934567890");
        order5.setRestaurantName("Pizza Hut");
        order5.setRestaurantAddress("234 Pasteur, Q3, TP.HCM");
        order5.setDeliveryAddress("567 Cách Mạng Tháng 8, Q10, TP.HCM");
        order5.setStatus("cancelled");
        order5.setTotalAmount(420000L);
        order5.setDeliveryFee(25000L);
        order5.setDistance(5.5f);
        order5.setOrderTime(new Date(System.currentTimeMillis() - 1800000)); // 30 phút trước
        order5.setEstimatedTime(40);
        order5.setGemsEarned(0); // No gems for cancelled orders
        order5.setTip(0L); // No tip for cancelled
        order5.setPaymentType("ONLINE");

        List<OrderItem> items5 = new ArrayList<>();
        items5.add(new OrderItem("Pizza Hải sản", 1, 320000));
        items5.add(new OrderItem("Pepsi", 2, 15000));
        order5.setItems(items5);
        
        orders.add(order5);
        
        // Order 6 - Accepted
        Order order6 = new Order();
        order6.setId("ORD006");
        order6.setCustomerName("Hoàng Văn Tùng");
        order6.setCustomerPhone("0945678901");
        order6.setRestaurantName("Gà Rán KFC");
        order6.setRestaurantAddress("123 Hai Bà Trưng, Q1, TP.HCM");
        order6.setDeliveryAddress("456 Nguyễn Đình Chiểu, Q3, TP.HCM");
        order6.setStatus("accepted");
        order6.setTotalAmount(250000L);
        order6.setDeliveryFee(22000L);
        order6.setDistance(2.8f);
        order6.setOrderTime(new Date(System.currentTimeMillis() - 600000)); // 10 phút trước
        order6.setEstimatedTime(20);
        order6.setGemsEarned(20);
        order6.setTip(3000L); // Small tip
        order6.setPaymentType("COD");

        List<OrderItem> items6 = new ArrayList<>();
        items6.add(new OrderItem("Gà rán giòn", 2, 85000));
        items6.add(new OrderItem("Khoai tây chiên", 1, 35000));
        items6.add(new OrderItem("Pepsi", 2, 15000));
        order6.setItems(items6);
        
        orders.add(order6);
        
        // Order 7 - Rejected
        Order order7 = new Order();
        order7.setId("ORD007");
        order7.setCustomerName("Đặng Thị Linh");
        order7.setCustomerPhone("0956789012");
        order7.setRestaurantName("Sushi Tokyo");
        order7.setRestaurantAddress("789 Lê Duẩn, Q1, TP.HCM");
        order7.setDeliveryAddress("321 Võ Thị Sáu, Q3, TP.HCM");
        order7.setStatus("rejected");
        order7.setTotalAmount(680000L);
        order7.setDeliveryFee(40000L);
        order7.setDistance(6.2f);
        order7.setOrderTime(new Date(System.currentTimeMillis() - 2400000)); // 40 phút trước
        order7.setEstimatedTime(45);
        order7.setGemsEarned(0); // No gems for rejected orders
        order7.setTip(0L); // No tip for rejected
        order7.setPaymentType("ONLINE");

        List<OrderItem> items7 = new ArrayList<>();
        items7.add(new OrderItem("Sushi Set A", 1, 450000));
        items7.add(new OrderItem("Miso Soup", 2, 35000));
        items7.add(new OrderItem("Trà xanh", 2, 25000));
        order7.setItems(items7);
        
        orders.add(order7);
        
        return orders;
    }
    
    public static List<Reward> createMockRewards() {
        List<Reward> rewards = new ArrayList<>();
        
        // Daily Reward 1 - In Progress
        Reward dailyReward1 = new Reward();
        dailyReward1.setId("RW001");
        dailyReward1.setType("daily");
        dailyReward1.setTitle("Hoàn thành 20 đơn hàng");
        dailyReward1.setDescription("Nhận 400 gems khi hoàn thành 20 đơn hàng trong ngày");
        dailyReward1.setGemsRequired(400);
        dailyReward1.setCashReward(50000L);
        dailyReward1.setCurrentProgress(18);
        dailyReward1.setTargetProgress(20);
        dailyReward1.setIsCompleted(false);
        dailyReward1.setExpiryDate(new Date(System.currentTimeMillis() + 86400000)); // 24 giờ
        rewards.add(dailyReward1);
        
        // Daily Reward 2 - In Progress
        Reward dailyReward2 = new Reward();
        dailyReward2.setId("RW002");
        dailyReward2.setType("daily");
        dailyReward2.setTitle("Hoàn thành 35 đơn hàng");
        dailyReward2.setDescription("Nhận 700 gems khi hoàn thành 35 đơn hàng trong ngày");
        dailyReward2.setGemsRequired(700);
        dailyReward2.setCashReward(200000L);
        dailyReward2.setCurrentProgress(18);
        dailyReward2.setTargetProgress(35);
        dailyReward2.setIsCompleted(false);
        dailyReward2.setExpiryDate(new Date(System.currentTimeMillis() + 86400000));
        rewards.add(dailyReward2);
        
        // Weekly Reward 1 - In Progress
        Reward weeklyReward1 = new Reward();
        weeklyReward1.setId("RW003");
        weeklyReward1.setType("weekly");
        weeklyReward1.setTitle("Hoàn thành 70 đơn hàng");
        weeklyReward1.setDescription("Nhận 1,400 gems khi hoàn thành 70 đơn hàng trong tuần");
        weeklyReward1.setGemsRequired(1400);
        weeklyReward1.setCashReward(100000L);
        weeklyReward1.setCurrentProgress(45);
        weeklyReward1.setTargetProgress(70);
        weeklyReward1.setIsCompleted(false);
        weeklyReward1.setExpiryDate(new Date(System.currentTimeMillis() + 604800000)); // 7 ngày
        rewards.add(weeklyReward1);
        
        // Weekly Reward 2 - In Progress
        Reward weeklyReward2 = new Reward();
        weeklyReward2.setId("RW004");
        weeklyReward2.setType("weekly");
        weeklyReward2.setTitle("Hoàn thành 125 đơn hàng");
        weeklyReward2.setDescription("Nhận 2,500 gems khi hoàn thành 125 đơn hàng trong tuần");
        weeklyReward2.setGemsRequired(2500);
        weeklyReward2.setCashReward(350000L);
        weeklyReward2.setCurrentProgress(45);
        weeklyReward2.setTargetProgress(125);
        weeklyReward2.setIsCompleted(false);
        weeklyReward2.setExpiryDate(new Date(System.currentTimeMillis() + 604800000));
        rewards.add(weeklyReward2);
        
        // Completed Reward - Example
        Reward completedReward = new Reward();
        completedReward.setId("RW005");
        completedReward.setType("daily");
        completedReward.setTitle("Hoàn thành 10 đơn hàng");
        completedReward.setDescription("Đã nhận 200 gems cho 10 đơn hàng đầu tiên");
        completedReward.setGemsRequired(200);
        completedReward.setCashReward(25000L);
        completedReward.setCurrentProgress(10);
        completedReward.setTargetProgress(10);
        completedReward.setIsCompleted(true);
        completedReward.setCompletedDate(new Date(System.currentTimeMillis() - 3600000)); // 1 giờ trước
        rewards.add(completedReward);
        
        return rewards;
    }
    
    public static Wallet createMockWallet(String shipperId) {
        Wallet wallet = new Wallet(shipperId);
        
        // Set mock wallet data based on shipper
        if (DEMO_PHONE.equals(shipperId) || "SH001".equals(shipperId)) {
            wallet.setCurrentBalance(750000L); // Above minimum - eligible for COD
            wallet.setCodHolding(180000L); // Has COD money to deposit
            wallet.setTodayEarnings(245000L);
            wallet.setWeekEarnings(1680000L);
            wallet.setTotalEarnings(45680000L);
        } else if (DEMO_PHONE2.equals(shipperId)) {
            wallet.setCurrentBalance(320000L); // Below minimum - only online orders
            wallet.setCodHolding(95000L);
            wallet.setTodayEarnings(180000L);
            wallet.setWeekEarnings(1250000L);
            wallet.setTotalEarnings(32450000L);
        } else if (DEMO_PHONE3.equals(shipperId)) {
            wallet.setCurrentBalance(890000L); // Well above minimum
            wallet.setCodHolding(0L); // No COD money
            wallet.setTodayEarnings(320000L);
            wallet.setWeekEarnings(2150000L);
            wallet.setTotalEarnings(58920000L);
        } else {
            // Default for new users
            wallet.setCurrentBalance(200000L); // Below minimum
            wallet.setCodHolding(50000L);
            wallet.setTodayEarnings(85000L);
            wallet.setWeekEarnings(450000L);
            wallet.setTotalEarnings(5000000L);
        }
        
        return wallet;
    }
    
    public static List<Transaction> createMockTransactions(String shipperId) {
        List<Transaction> transactions = new ArrayList<>();
        
        // Recent earning transaction
        Transaction earning1 = Transaction.createEarningTransaction(shipperId, "ORD003", 30000L, 5000L);
        earning1.setId("TXN001");
        earning1.setTransactionDate(new Date(System.currentTimeMillis() - 3600000)); // 1 hour ago
        transactions.add(earning1);
        
        // Another earning transaction
        Transaction earning2 = Transaction.createEarningTransaction(shipperId, "ORD002", 20000L, 0L);
        earning2.setId("TXN002");
        earning2.setTransactionDate(new Date(System.currentTimeMillis() - 7200000)); // 2 hours ago
        transactions.add(earning2);
        
        // COD deposit transaction
        Transaction codDeposit = Transaction.createCodDepositTransaction(shipperId, 150000L);
        codDeposit.setId("TXN003");
        codDeposit.setTransactionDate(new Date(System.currentTimeMillis() - 10800000)); // 3 hours ago
        transactions.add(codDeposit);
        
        // Top-up transaction
        Transaction topUp = Transaction.createTopUpTransaction(shipperId, 500000L);
        topUp.setId("TXN004");
        topUp.setTransactionDate(new Date(System.currentTimeMillis() - 86400000)); // 1 day ago
        transactions.add(topUp);
        
        // Bonus transaction
        Transaction bonus = Transaction.createBonusTransaction(shipperId, 50000L, "Hoàn thành 20 đơn hàng");
        bonus.setId("TXN005");
        bonus.setTransactionDate(new Date(System.currentTimeMillis() - 172800000)); // 2 days ago
        transactions.add(bonus);
        
        // More earning transactions for history
        Transaction earning3 = Transaction.createEarningTransaction(shipperId, "ORD001", 25000L, 10000L);
        earning3.setId("TXN006");
        earning3.setTransactionDate(new Date(System.currentTimeMillis() - 259200000)); // 3 days ago
        transactions.add(earning3);
        
        Transaction earning4 = Transaction.createEarningTransaction(shipperId, "ORD008", 35000L, 0L);
        earning4.setId("TXN007");
        earning4.setTransactionDate(new Date(System.currentTimeMillis() - 345600000)); // 4 days ago
        transactions.add(earning4);
        
        // Weekend bonus
        Transaction weekendBonus = Transaction.createBonusTransaction(shipperId, 75000L, "Làm việc cuối tuần");
        weekendBonus.setId("TXN008");
        weekendBonus.setTransactionDate(new Date(System.currentTimeMillis() - 432000000)); // 5 days ago
        transactions.add(weekendBonus);
        
        // Peak hour bonus
        Transaction peakBonus = Transaction.createBonusTransaction(shipperId, 30000L, "Giờ cao điểm");
        peakBonus.setId("TXN009");
        peakBonus.setTransactionDate(new Date(System.currentTimeMillis() - 518400000)); // 6 days ago
        transactions.add(peakBonus);
        
        // Another COD deposit
        Transaction codDeposit2 = Transaction.createCodDepositTransaction(shipperId, 200000L);
        codDeposit2.setId("TXN010");
        codDeposit2.setTransactionDate(new Date(System.currentTimeMillis() - 604800000)); // 1 week ago
        transactions.add(codDeposit2);
        
        return transactions;
    }
} 