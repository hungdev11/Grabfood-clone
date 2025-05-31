-- Kiểm tra tất cả reward claims
SELECT 
    sr.id,
    s.name as shipper_name,
    s.phone,
    r.title as reward_title,
    r.type as reward_type,
    sr.status,
    sr.claimed_at,
    sr.completion_percentage
FROM shipper_reward sr 
JOIN shipper s ON sr.shipper_id = s.id 
JOIN reward r ON sr.reward_id = r.id
ORDER BY sr.claimed_at DESC;

-- Kiểm tra rewards available
SELECT 
    id,
    title,
    type,
    status,
    required_orders,
    required_distance,
    required_rating,
    reward_value,
    gems_value,
    start_date,
    end_date
FROM reward 
WHERE status = 'ACTIVE'
ORDER BY id;

-- Kiểm tra shipper cụ thể (thay phone number)
SELECT 
    s.id as shipper_id,
    s.name,
    s.phone,
    COUNT(sr.id) as total_claims,
    COUNT(CASE WHEN sr.status = 'CLAIMED' THEN 1 END) as claimed_rewards
FROM shipper s
LEFT JOIN shipper_reward sr ON s.id = sr.shipper_id
WHERE s.phone = '0987654321' -- Thay bằng phone của shipper bạn test
GROUP BY s.id, s.name, s.phone; 