-- Reset tất cả reward claims (để test lại)
DELETE FROM shipper_reward;

-- Hoặc chỉ reset cho một shipper cụ thể
-- DELETE FROM shipper_reward 
-- WHERE shipper_id = (SELECT id FROM shipper WHERE phone = '0987654321');

-- Kiểm tra sau khi reset
SELECT COUNT(*) as remaining_claims FROM shipper_reward; 