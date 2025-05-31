-- Debug script để kiểm tra logic reward calculation
USE `grab-food`;

-- 1. Kiểm tra shipper đang test (phone: 0111111111)
SELECT 
    s.id,
    s.name,
    s.phone,
    s.rating,
    s.completed_orders,
    s.total_orders,
    s.status,
    s.is_online
FROM shipper s 
WHERE s.phone = '0111111111';

-- 2. Kiểm tra orders của shipper 1 theo trạng thái
SELECT 
    status,
    COUNT(*) as count,
    GROUP_CONCAT(id ORDER BY id) as order_ids
FROM orders 
WHERE shipper_id = 1
GROUP BY status;

-- 3. Kiểm tra orders COMPLETED của shipper 1 theo thời gian
SELECT 
    id,
    order_date,
    status,
    delivered_at,
    distance,
    'TODAY' as period
FROM orders 
WHERE shipper_id = 1 
    AND status = 'COMPLETED'
    AND DATE(order_date) = CURDATE()

UNION ALL

SELECT 
    id,
    order_date, 
    status,
    delivered_at,
    distance,
    'THIS_WEEK' as period
FROM orders 
WHERE shipper_id = 1 
    AND status = 'COMPLETED'
    AND WEEK(order_date) = WEEK(NOW())
    
UNION ALL

SELECT 
    id,
    order_date,
    status, 
    delivered_at,
    distance,
    'THIS_MONTH' as period
FROM orders 
WHERE shipper_id = 1 
    AND status = 'COMPLETED'
    AND MONTH(order_date) = MONTH(NOW())
    AND YEAR(order_date) = YEAR(NOW())

ORDER BY period, order_date;

-- 4. Kiểm tra reward eligibility cho từng reward
SELECT 
    r.id as reward_id,
    r.title,
    r.type,
    r.required_orders,
    r.required_rating,
    r.required_distance,
    -- Count orders theo type
    CASE r.type
        WHEN 'DAILY' THEN (
            SELECT COUNT(*) FROM orders o 
            WHERE o.shipper_id = 1 
                AND o.status = 'COMPLETED'
                AND DATE(o.order_date) = CURDATE()
        )
        WHEN 'WEEKLY' THEN (
            SELECT COUNT(*) FROM orders o 
            WHERE o.shipper_id = 1 
                AND o.status = 'COMPLETED'
                AND WEEK(o.order_date) = WEEK(NOW())
        )
        WHEN 'MONTHLY' THEN (
            SELECT COUNT(*) FROM orders o 
            WHERE o.shipper_id = 1 
                AND o.status = 'COMPLETED'
                AND MONTH(o.order_date) = MONTH(NOW())
                AND YEAR(o.order_date) = YEAR(NOW())
        )
        ELSE (
            SELECT COUNT(*) FROM orders o 
            WHERE o.shipper_id = 1 
                AND o.status = 'COMPLETED'
        )
    END as current_orders,
    s.rating as current_rating,
    -- Check eligibility
    CASE 
        WHEN r.required_orders IS NOT NULL AND r.required_rating IS NOT NULL THEN
            CASE 
                WHEN (
                    CASE r.type
                        WHEN 'DAILY' THEN (SELECT COUNT(*) FROM orders o WHERE o.shipper_id = 1 AND o.status = 'COMPLETED' AND DATE(o.order_date) = CURDATE())
                        WHEN 'WEEKLY' THEN (SELECT COUNT(*) FROM orders o WHERE o.shipper_id = 1 AND o.status = 'COMPLETED' AND WEEK(o.order_date) = WEEK(NOW()))
                        WHEN 'MONTHLY' THEN (SELECT COUNT(*) FROM orders o WHERE o.shipper_id = 1 AND o.status = 'COMPLETED' AND MONTH(o.order_date) = MONTH(NOW()) AND YEAR(o.order_date) = YEAR(NOW()))
                        ELSE (SELECT COUNT(*) FROM orders o WHERE o.shipper_id = 1 AND o.status = 'COMPLETED')
                    END >= r.required_orders
                ) AND s.rating >= r.required_rating THEN 'ELIGIBLE'
                ELSE 'NOT_ELIGIBLE'
            END
        WHEN r.required_orders IS NOT NULL THEN
            CASE 
                WHEN (
                    CASE r.type
                        WHEN 'DAILY' THEN (SELECT COUNT(*) FROM orders o WHERE o.shipper_id = 1 AND o.status = 'COMPLETED' AND DATE(o.order_date) = CURDATE())
                        WHEN 'WEEKLY' THEN (SELECT COUNT(*) FROM orders o WHERE o.shipper_id = 1 AND o.status = 'COMPLETED' AND WEEK(o.order_date) = WEEK(NOW()))
                        WHEN 'MONTHLY' THEN (SELECT COUNT(*) FROM orders o WHERE o.shipper_id = 1 AND o.status = 'COMPLETED' AND MONTH(o.order_date) = MONTH(NOW()) AND YEAR(o.order_date) = YEAR(NOW()))
                        ELSE (SELECT COUNT(*) FROM orders o WHERE o.shipper_id = 1 AND o.status = 'COMPLETED')
                    END >= r.required_orders
                ) THEN 'ELIGIBLE'
                ELSE 'NOT_ELIGIBLE'
            END
        ELSE 'ELIGIBLE'
    END as eligibility_status
FROM rewards r
CROSS JOIN shipper s
WHERE r.status = 'ACTIVE' AND s.id = 1
ORDER BY r.id;

-- 5. Kiểm tra claims hiện tại
SELECT 
    sr.id,
    sr.shipper_id,
    r.title,
    sr.status,
    sr.claimed_at,
    sr.completion_percentage
FROM shipper_rewards sr
JOIN rewards r ON sr.reward_id = r.id
WHERE sr.shipper_id = 1;

-- 6. Tổng hợp để test
SELECT 
    'Summary for Shipper 1' as title,
    (SELECT COUNT(*) FROM orders WHERE shipper_id = 1 AND status = 'COMPLETED') as total_completed_orders,
    (SELECT COUNT(*) FROM orders WHERE shipper_id = 1 AND status = 'COMPLETED' AND DATE(order_date) = CURDATE()) as today_completed_orders,
    (SELECT rating FROM shipper WHERE id = 1) as current_rating,
    (SELECT COUNT(*) FROM rewards WHERE status = 'ACTIVE') as active_rewards,
    (SELECT COUNT(*) FROM shipper_rewards WHERE shipper_id = 1) as current_claims; 