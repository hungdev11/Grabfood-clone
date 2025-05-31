-- SIMPLE SETUP FOR REWARD TESTING
USE `grab-food`;

-- 1. Xóa claims cũ của shipper 1
DELETE FROM shipper_rewards WHERE shipper_id = 1;

-- 2. Cập nhật orders hiện tại thành COMPLETED cho shipper 1  
UPDATE orders 
SET status = 'COMPLETED',
    delivered_at = '2025-05-31 10:00:00'
WHERE id IN (91, 92, 93, 94, 95) AND shipper_id = 1;

-- 3. Thêm thêm 5 orders COMPLETED nữa để đủ 10 orders (cho reward 1)
INSERT INTO orders (
    address, note, order_date, shipping_fee, status, total_price, user_id,
    discount_order_price, discount_shipping_fee, shipper_id, delivered_at,
    delivery_latitude, delivery_longitude, distance, payment_method
) VALUES 
('Test Address A', 'Test A', '2025-05-31 08:00:00', 15000, 'COMPLETED', 50000, 1, 0, 0, 1, '2025-05-31 10:00:00', 10.77, 106.70, 5.0, 'COD'),
('Test Address B', 'Test B', '2025-05-31 08:30:00', 18000, 'COMPLETED', 45000, 2, 0, 0, 1, '2025-05-31 10:30:00', 10.78, 106.71, 6.0, 'COD'),
('Test Address C', 'Test C', '2025-05-31 09:00:00', 20000, 'COMPLETED', 55000, 3, 0, 0, 1, '2025-05-31 11:00:00', 10.79, 106.72, 7.0, 'COD'),
('Test Address D', 'Test D', '2025-05-31 09:30:00', 22000, 'COMPLETED', 42000, 1, 0, 0, 1, '2025-05-31 11:30:00', 10.80, 106.73, 8.0, 'COD'),
('Test Address E', 'Test E', '2025-05-31 10:00:00', 25000, 'COMPLETED', 48000, 2, 0, 0, 1, '2025-05-31 12:00:00', 10.81, 106.74, 9.0, 'COD');

-- 4. Cập nhật shipper stats
UPDATE shipper 
SET completed_orders = 10,
    total_orders = 10,
    rating = 4.85,
    is_online = 1,
    status = 'ACTIVE'
WHERE id = 1;

-- 5. Kiểm tra kết quả
SELECT 'SHIPPER INFO' as section;
SELECT id, name, phone, rating, completed_orders, total_orders, status FROM shipper WHERE id = 1;

SELECT 'COMPLETED ORDERS COUNT' as section;
SELECT COUNT(*) as completed_orders FROM orders WHERE shipper_id = 1 AND status = 'COMPLETED';

SELECT 'REWARDS AVAILABLE' as section;
SELECT id, title, required_orders, required_rating, status FROM rewards WHERE status = 'ACTIVE' ORDER BY required_orders;

SELECT 'ELIGIBILITY CHECK FOR REWARD 1 (10 orders, 4.5 rating)' as section;
SELECT 
    (SELECT COUNT(*) FROM orders WHERE shipper_id = 1 AND status = 'COMPLETED') as current_orders,
    (SELECT rating FROM shipper WHERE id = 1) as current_rating,
    CASE 
        WHEN (SELECT COUNT(*) FROM orders WHERE shipper_id = 1 AND status = 'COMPLETED') >= 10 
         AND (SELECT rating FROM shipper WHERE id = 1) >= 4.5
        THEN 'ELIGIBLE FOR REWARD 1' 
        ELSE 'NOT ELIGIBLE' 
    END as result;

SELECT 'ELIGIBILITY CHECK FOR REWARD 2 (5 orders, 4.0 rating)' as section;
SELECT 
    (SELECT COUNT(*) FROM orders WHERE shipper_id = 1 AND status = 'COMPLETED') as current_orders,
    (SELECT rating FROM shipper WHERE id = 1) as current_rating,
    CASE 
        WHEN (SELECT COUNT(*) FROM orders WHERE shipper_id = 1 AND status = 'COMPLETED') >= 5 
         AND (SELECT rating FROM shipper WHERE id = 1) >= 4.0
        THEN 'ELIGIBLE FOR REWARD 2' 
        ELSE 'NOT ELIGIBLE' 
    END as result; 