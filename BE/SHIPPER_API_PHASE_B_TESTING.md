# 🚀 **Phase B - Shipper Management APIs Testing Guide**

## **Base URL:** `http://localhost:6969/grab`

## **Prerequisites:**
1. ✅ Application running on port 6969
2. ✅ Valid shipper JWT token from Phase A login
3. ✅ Database connection established

---

## **📋 API Endpoints Summary (Phase B)**

| **API** | **Method** | **Endpoint** | **Auth** | **Description** |
|---------|-----------|-------------|----------|-----------------|
| **1. Get Shipper Profile** | `GET` | `/api/shippers/profile` | SHIPPER | Lấy thông tin chi tiết shipper |
| **2. Update Profile** | `PUT` | `/api/shippers/profile` | SHIPPER | Cập nhật thông tin cá nhân |
| **3. Update Status** | `PUT` | `/api/shippers/status` | SHIPPER | Bật/tắt online/offline |
| **4. Update Location** | `PUT` | `/api/shippers/location` | SHIPPER | Cập nhật GPS thời gian thực |

---

## **🔐 Authentication Required**
All APIs in Phase B require `Authorization: Bearer <jwt_token>`

### **Step 1: Get JWT Token from Phase A**
```bash
# PowerShell
$loginBody = Get-Content test_shipper_login.json -Raw
$loginResponse = Invoke-RestMethod -Uri "http://localhost:6969/grab/api/auth/shipper/login" -Method POST -Body $loginBody -ContentType "application/json"
$token = $loginResponse.token
Write-Host "Token: $token"
```

---

## **🧪 Detailed Testing Instructions**

### **1. GET /api/shippers/profile - Get Shipper Profile**

**PowerShell Command:**
```bash
$headers = @{ Authorization = "Bearer $token" }
$response = Invoke-RestMethod -Uri "http://localhost:6969/grab/api/shippers/profile" -Method GET -Headers $headers
$response | ConvertTo-Json -Depth 10
```

**Expected Response:**
```json
{
  "id": 1,
  "name": "Nguyen Van A",
  "phone": "0987654321",
  "email": "shipper@example.com",
  "isOnline": false,
  "status": "ACTIVE",
  "rating": 5.00,
  "totalOrders": 0,
  "completedOrders": 0,
  "acceptanceRate": 100.0,
  "cancellationRate": 0.0,
  "totalEarnings": 0,
  "gems": 0,
  "vehicleType": "Motorbike",
  "vehicleNumber": "29A1-12345",
  "currentLatitude": null,
  "currentLongitude": null,
  "createdDate": "2024-01-01T00:00:00"
}
```

---

### **2. PUT /api/shippers/profile - Update Profile**

**PowerShell Command:**
```bash
$headers = @{ Authorization = "Bearer $token" }
$updateBody = Get-Content test_update_shipper_profile.json -Raw
$response = Invoke-RestMethod -Uri "http://localhost:6969/grab/api/shippers/profile" -Method PUT -Body $updateBody -ContentType "application/json" -Headers $headers
$response | ConvertTo-Json -Depth 10
```

**Request Body (test_update_shipper_profile.json):**
```json
{
    "name": "Nguyen Van B Updated",
    "email": "nguyenvanb_updated@gmail.com",
    "vehicleType": "Motorbike",
    "vehicleNumber": "29A1-12345",
    "licensePlate": "29A1-12345"
}
```

**Expected Response:** Updated ShipperProfileResponse with new values

---

### **3. PUT /api/shippers/status - Update Online/Offline Status**

**PowerShell Command:**
```bash
$headers = @{ Authorization = "Bearer $token" }
$statusBody = Get-Content test_update_shipper_status.json -Raw
$response = Invoke-RestMethod -Uri "http://localhost:6969/grab/api/shippers/status" -Method PUT -Body $statusBody -ContentType "application/json" -Headers $headers
$response
```

**Request Body (test_update_shipper_status.json):**
```json
{
    "isOnline": true
}
```

**Expected Response:**
```json
{
  "message": "Status updated successfully",
  "isOnline": true,
  "shipperId": 1
}
```

---

### **4. PUT /api/shippers/location - Update GPS Location**

**PowerShell Command:**
```bash
$headers = @{ Authorization = "Bearer $token" }
$locationBody = Get-Content test_update_shipper_location.json -Raw
$response = Invoke-RestMethod -Uri "http://localhost:6969/grab/api/shippers/location" -Method PUT -Body $locationBody -ContentType "application/json" -Headers $headers
$response
```

**Request Body (test_update_shipper_location.json):**
```json
{
    "latitude": 10.8231,
    "longitude": 106.6297
}
```

**Expected Response:**
```json
{
  "message": "Location updated successfully",
  "latitude": 10.8231,
  "longitude": 106.6297,
  "shipperId": 1
}
```

---

## **🚨 Common Error Scenarios**

### **401 Unauthorized**
- Missing or invalid JWT token
- Token expired
- Wrong role (not SHIPPER)

### **400 Bad Request**
- Missing required fields (latitude/longitude for location update)
- Invalid JSON format

### **404 Not Found**
- Shipper not found in database
- Invalid endpoint URL

### **500 Internal Server Error**
- Database connection issues
- Service layer errors

---

## **✅ Success Criteria**

Phase B is successful when:
1. ✅ All 4 APIs return proper JSON responses
2. ✅ Profile updates persist in database
3. ✅ Status changes reflect in shipper record
4. ✅ Location updates save both lat/lng and lat/lon fields
5. ✅ Authentication properly validates SHIPPER role

---

## **📝 Notes**

- All responses are in JSON format for Android compatibility
- Location updates support both database field formats (latitude/longitude and lat/lon)
- Profile updates only change fields that are provided (partial updates)
- Status updates immediately affect shipper availability for orders

**Next Phase:** C. Order Management APIs (7 endpoints) 