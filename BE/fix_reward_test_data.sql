-- Fix dữ liệu để test reward system
USE `grab-food`;

-- 1. Cập nhật một số orders thành COMPLETED cho shipper 1
UPDATE orders 
SET status = 'COMPLETED',
    delivered_at = NOW() - INTERVAL FLOOR(RAND() * 30) DAY
WHERE id IN (91, 92, 93, 94, 95, 84, 85) 
AND shipper_id = 1;

-- 2. Thêm một số orders COMPLETED cho shipper 1 (để đủ 10 orders cho reward 1)
INSERT INTO orders (
    id, address, note, order_date, shipping_fee, status, total_price, user_id, 
    discount_order_price, discount_shipping_fee, shipper_id, delivered_at,
    delivery_latitude, delivery_longitude, distance, payment_method
) VALUES 
(96, 'Test Address 6', 'Completed Order 6', '2025-05-30 10:00:00', 15000, 'COMPLETED', 45000, 1, 0, 0, 1, '2025-05-30 12:00:00', 10.77, 106.70, 5.5, 'COD'),
(97, 'Test Address 7', 'Completed Order 7', '2025-05-30 11:00:00', 18000, 'COMPLETED', 52000, 2, 0, 0, 1, '2025-05-30 13:00:00', 10.78, 106.71, 6.2, 'VNPAY'),
(98, 'Test Address 8', 'Completed Order 8', '2025-05-30 12:00:00', 20000, 'COMPLETED', 38000, 3, 0, 0, 1, '2025-05-30 14:00:00', 10.79, 106.72, 7.1, 'COD'),
(99, 'Test Address 9', 'Completed Order 9', '2025-05-30 13:00:00', 22000, 'COMPLETED', 65000, 1, 0, 0, 1, '2025-05-30 15:00:00', 10.80, 106.73, 8.3, 'VNPAY'),
(100, 'Test Address 10', 'Completed Order 10', '2025-05-30 14:00:00', 25000, 'COMPLETED', 71000, 2, 0, 0, 1, '2025-05-30 16:00:00', 10.81, 106.74, 9.2, 'COD');

-- 3. Cập nhật completed_orders cho shipper 1
UPDATE shipper 
SET completed_orders = 12,
    total_orders = 12,
    rating = 4.85  -- Đảm bảo rating đủ cho reward 1
WHERE id = 1;

-- 4. Xóa các reward claims cũ để test lại
DELETE FROM shipper_rewards WHERE shipper_id = 1;

-- 5. Kiểm tra kết quả
SELECT 
    s.id as shipper_id,
    s.name,
    s.phone,
    s.rating,
    s.completed_orders,
    s.total_orders
FROM shipper s 
WHERE s.id = 1;

-- 6. Kiểm tra orders completed của shipper 1
SELECT 
    COUNT(*) as completed_orders_count,
    GROUP_CONCAT(id) as order_ids
FROM orders 
WHERE shipper_id = 1 AND status = 'COMPLETED';

-- 7. Kiểm tra rewards available
SELECT 
    id,
    title,
    required_orders,
    required_rating,
    status
FROM rewards 
WHERE status = 'ACTIVE'
ORDER BY required_orders;

-- 8. Test với reward đơn giản nhất (reward 2: 5 orders, rating 4.0)
SELECT 
    'Shipper 1 should be eligible for reward 2' as note,
    s.completed_orders as 'completed_orders',
    r.required_orders as 'required_orders',
    s.rating as 'current_rating', 
    r.required_rating as 'required_rating',
    CASE 
        WHEN s.completed_orders >= r.required_orders AND s.rating >= r.required_rating 
        THEN 'ELIGIBLE' 
        ELSE 'NOT_ELIGIBLE' 
    END as eligibility
FROM shipper s
CROSS JOIN rewards r
WHERE s.id = 1 AND r.id = 2; 