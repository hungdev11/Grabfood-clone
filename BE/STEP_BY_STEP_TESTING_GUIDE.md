# 🔧 COMPLETE SHIPPER API TESTING GUIDE

## **🎯 OVERVIEW**

Sau khi phân tích toàn bộ code và database, tôi đã xác định được **ROOT CAUSE** và tạo **COMPLETE FIX**.

### **🔴 ROOT CAUSE:**
- **Database không sync**: Orders PENDING không có `shipper_id` trong bảng `orders`
- **Trigger không hoạt động đúng**: Data ở `order_assignment` không được sync vào `orders.shipper_id`
- **Missing test data**: Thiếu orders được assign cho shipper để test

### **✅ SOLUTION:**
- **Complete Database Fix Script**: `COMPLETE_DATABASE_DEBUG_AND_FIX.sql`
- **Recreate Triggers**: Đảm bảo sync data đúng
- **Create Test Data**: Tạo orders được assign cho shipper 1

---

## **📋 STEP-BY-STEP TESTING**

### **STEP 1: CHẠY DATABASE FIX SCRIPT**

```bash
# Mở MySQL Workbench hoặc command line
mysql -u root -p grab-food < COMPLETE_DATABASE_DEBUG_AND_FIX.sql
```

**Expected Result:**
- Script sẽ debug hiện trạng database
- Fix tất cả vấn đề sync data
- Tạo test data cho shipper 1
- Verify kết quả fix

### **STEP 2: START SPRING BOOT APPLICATION**

```bash
# Trong thư mục BE
mvn spring-boot:run

# Hoặc nếu đã build jar
java -jar target/grabfood-api.jar
```

**Application sẽ chạy tại:**
- **URL**: `http://localhost:6969/grab`
- **Database**: `grab-food` on localhost:3306

### **STEP 3: TEST AUTHENTICATION**

**Login Request:**
```bash
POST http://localhost:6969/grab/api/auth/shipper/login
Content-Type: application/json

{
  "phone": "0111111111",
  "password": "password123"
}
```

**Expected Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "message": "Authentication successful", 
  "phone": "0111111111",
  "shipperId": 1,
  "name": "Marcus Grabfood",
  "status": "ACTIVE",
  "isOnline": true
}
```

**✅ Authentication SHOULD WORK** vì:
- Shipper 1 có account với username = "0111111111"
- Password đã được hash trong database
- Role = ROLE_SHIPPER (id=4)

### **STEP 4: TEST GET ORDERS API**

**Copy TOKEN từ Step 3**, sau đó test:

#### **4.1. Get All Orders for Shipper**
```bash
GET http://localhost:6969/grab/api/orders
Authorization: Bearer YOUR_TOKEN_HERE
```

#### **4.2. Get PENDING Orders Only**
```bash
GET http://localhost:6969/grab/api/orders?status=PENDING
Authorization: Bearer YOUR_TOKEN_HERE
```

#### **4.3. Get Orders with Pagination**
```bash
GET http://localhost:6969/grab/api/orders?status=PENDING&page=0&size=5
Authorization: Bearer YOUR_TOKEN_HERE
```

**Expected Response Structure:**
```json
{
  "code": 200,
  "message": "Success",
  "data": {
    "content": [
      {
        "id": 84,
        "customerName": "Nguyễn Văn A",
        "customerPhone": "0111111111",
        "address": "Test Address for Shipper API",
        "status": "PENDING",
        "totalPrice": 50000.00,
        "shippingFee": 15000.00,
        "paymentMethod": "COD",
        "restaurantName": "Urban Flavor",
        "items": [...]
      }
    ],
    "totalElements": 2,
    "totalPages": 1,
    "size": 20,
    "number": 0
  }
}
```

### **STEP 5: TEST OTHER SHIPPER APIs**

#### **5.1. Get Shipper Profile**
```bash
GET http://localhost:6969/grab/api/shippers/profile
Authorization: Bearer YOUR_TOKEN_HERE
```

#### **5.2. Get Order Detail**
```bash
GET http://localhost:6969/grab/api/orders/{orderId}
Authorization: Bearer YOUR_TOKEN_HERE
```

#### **5.3. Accept Order**
```bash
PUT http://localhost:6969/grab/api/orders/{orderId}/accept
Authorization: Bearer YOUR_TOKEN_HERE
```

---

## **🐛 TROUBLESHOOTING**

### **Error 1: "Order not found"**
**Cause**: Không có orders được assign cho shipper
**Fix**: Chạy script SQL để tạo test data

### **Error 2: "Authentication failed"**
**Cause**: Token không đúng hoặc account không active
**Fix**: 
```sql
UPDATE account SET is_active = 1 WHERE username = '0111111111';
```

### **Error 3: "No enum constant PaymentMethod.ONLINE"**
**Cause**: Database có payment_method = 'ONLINE' nhưng enum không support
**Fix**: Đã được fix trong script chính

### **Error 4: 500 Internal Server Error**
**Cause**: Database connection issues
**Fix**: Kiểm tra application.yml:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/grab-food
    username: root
    password: root
```

---

## **📊 VERIFICATION QUERIES**

Chạy các query này trong MySQL để verify fix:

```sql
-- 1. Kiểm tra orders được assign cho shipper 1
SELECT id, status, shipper_id, assigned_at, total_price
FROM orders 
WHERE shipper_id = 1
ORDER BY order_date DESC;

-- 2. Kiểm tra orders PENDING có shipper
SELECT COUNT(*) as pending_with_shipper
FROM orders 
WHERE status = 'PENDING' AND shipper_id IS NOT NULL;

-- 3. Kiểm tra account shipper 1
SELECT a.username, a.is_active, s.name, s.status
FROM account a
JOIN shipper s ON s.account_id = a.id
WHERE a.username = '0111111111';
```

---

## **✅ SUCCESS CRITERIA**

**API sẽ work correctly khi:**

1. ✅ **Login successful**: Trả về token
2. ✅ **GET /orders**: Trả về list orders (không empty)
3. ✅ **GET /orders?status=PENDING**: Trả về PENDING orders 
4. ✅ **Response format**: Đúng structure với ShipperOrderResponse
5. ✅ **No 500 errors**: Tất cả APIs trả về 200 hoặc error code có ý nghĩa

---

## **🔄 IF STILL FAILS**

Nếu vẫn lỗi, chạy **comprehensive debug**:

```sql
-- Debug query để kiểm tra logic
SELECT 'Orders query test:' AS info;
SELECT o.id, o.status, o.shipper_id, s.phone, s.name
FROM orders o
LEFT JOIN shipper s ON o.shipper_id = s.id
WHERE s.phone = '0111111111'
ORDER BY o.order_date DESC;

-- Kiểm tra có data không
SELECT COUNT(*) as total_orders_for_shipper_1
FROM orders WHERE shipper_id = 1;
```

**Gửi kết quả query này** nếu API vẫn trả về lỗi để tôi debug tiếp. 