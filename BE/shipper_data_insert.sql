-- ===================================
-- SCRIPT THÊM DỮ LIỆU CHO SHIPPER SYSTEM
-- ===================================

USE `grab-food`;

-- ===================================
-- 1. THÊM DỮ LIỆU CHO ORDER_ASSIGNMENT
-- (Quản lý việc assign orders cho shippers)
-- ===================================

INSERT INTO `order_assignment` (`assigned_at`, `estimated_delivery_time`, `estimated_pickup_time`, `rejection_reason`, `responded_at`, `status`, `order_id`, `shipper_id`) VALUES
-- Assignment 1: Order đã được accept
('2025-04-18 08:00:00.000000', '2025-04-18 09:00:00.000000', '2025-04-18 08:30:00.000000', NULL, '2025-04-18 08:05:00.000000', 'ACCEPTED', 22, 1),

-- Assignment 2: Order bị reject
('2025-04-18 09:15:00.000000', '2025-04-18 10:15:00.000000', '2025-04-18 09:45:00.000000', 'Xa quá, không đi được', '2025-04-18 09:20:00.000000', 'REJECTED', 23, 2),

-- Assignment 3: Order vừa được assign, chưa response
('2025-04-18 10:30:00.000000', '2025-04-18 11:30:00.000000', '2025-04-18 11:00:00.000000', NULL, NULL, 'ASSIGNED', 24, 1);

-- ===================================
-- 2. CẬP NHẬT ORDERS ĐỂ CÓ SHIPPER_ID
-- (Để test các API orders của shipper)
-- ===================================

-- Cập nhật một số orders để có shipper assigned
UPDATE `orders` SET `shipper_id` = 1, `status` = 'PROCESSING' WHERE `id` = 22;
UPDATE `orders` SET `shipper_id` = 1, `status` = 'SHIPPING' WHERE `id` = 26;
UPDATE `orders` SET `shipper_id` = 2, `status` = 'COMPLETED', `delivered_at` = '2025-04-18 12:00:00.000000' WHERE `id` = 27;

-- ===================================
-- 3. THÊM DỮ LIỆU CHO REWARDS
-- (Hệ thống phần thưởng cho shipper)
-- ===================================

INSERT INTO `rewards` (`amount`, `description`, `is_active`, `name`, `peak_end_time`, `peak_start_time`, `required_deliveries`, `type`, `valid_from`, `valid_to`) VALUES
-- Reward 1: Daily bonus
(50000.00, 'Hoàn thành 10 đơn hàng trong ngày để nhận thưởng 50K', b'1', 'Thưởng Hàng Ngày', NULL, NULL, 10, 'DAILY', '2025-04-01', '2025-12-31'),

-- Reward 2: Peak hour bonus  
(30000.00, 'Thưởng giờ cao điểm: Giao hàng trong khung giờ 11:00-13:00 để nhận thêm 30K', b'1', 'Thưởng Giờ Cao Điểm', '13:00:00.000000', '11:00:00.000000', 1, 'PEAK_HOUR', '2025-04-01', '2025-12-31'),

-- Reward 3: Milestone bonus
(200000.00, 'Cột mốc 100 đơn hàng: Hoàn thành 100 đơn hàng để nhận thưởng lớn 200K', b'1', 'Cột Mốc 100 Đơn', NULL, NULL, 100, 'MILESTONE', '2025-04-01', '2025-12-31');

-- ===================================
-- 4. THÊM DỮ LIỆU CHO TRANSACTION
-- (Lịch sử giao dịch của shipper)
-- ===================================

INSERT INTO `transaction` (`amount`, `commission`, `delivery_fee`, `description`, `net_amount`, `order_id`, `status`, `tip`, `transaction_date`, `type`, `shipper_id`) VALUES
-- Transaction 1: Earning từ delivery
(25000, 3750, 25000, 'Thu nhập từ giao đơn hàng #22', 21250, 22, 'COMPLETED', 0, '2025-04-18 08:45:00.000000', 'EARNING', 1),

-- Transaction 2: COD deposit
(150000, 0, 0, 'Nộp tiền COD từ đơn hàng #26', 150000, 26, 'COMPLETED', 0, '2025-04-18 09:30:00.000000', 'COD_DEPOSIT', 1),

-- Transaction 3: Tip từ khách hàng
(20000, 0, 0, 'Tip từ khách hàng đơn hàng #27', 20000, 27, 'COMPLETED', 20000, '2025-04-18 12:15:00.000000', 'TIP', 2);

-- ===================================
-- 5. THÊM WALLET CHO SHIPPER THIẾU
-- (Đảm bảo mọi shipper đều có wallet)
-- ===================================

INSERT INTO `wallet` (`cod_holding`, `current_balance`, `is_eligible_for_cod`, `last_updated`, `month_earnings`, `today_earnings`, `total_earnings`, `week_earnings`, `shipper_id`) VALUES
-- Wallet cho shipper ID 1
(50000, 450000, b'1', '2025-04-18 15:00:00.000000', 1200000, 65000, 3400000, 280000, 1);

-- ===================================
-- 6. CẬP NHẬT DELIVERY_FEES NẾU CHƯA CÓ
-- (Đảm bảo có bảng phí delivery)
-- ===================================

-- Kiểm tra và thêm delivery fees nếu cần
INSERT IGNORE INTO `delivery_fees` (`base_fee`, `is_active`, `max_distance`, `min_distance`) VALUES
(8000.00, b'1', 1.00, 0.00),
(12000.00, b'1', 3.00, 1.00), 
(20000.00, b'1', 7.00, 3.00),
(30000.00, b'1', 15.00, 7.00),
(40000.00, b'1', NULL, 15.00);

-- ===================================
-- 7. THÊM ACCOUNT_NOTIFICATION CHO SHIPPER
-- (Notifications cho shipper)
-- ===================================

-- Tạo notifications mẫu
INSERT INTO `notifications` (`body`, `date`, `subject`, `type`) VALUES
('Bạn có đơn hàng mới cần xác nhận. Vui lòng kiểm tra và phản hồi trong 5 phút.', '2025-04-18 10:30:00.000000', 'Đơn hàng mới', 'NEW_ORDER'),
('Đơn hàng #22 đã chuyển sang trạng thái giao hàng. Hãy lấy hàng tại nhà hàng.', '2025-04-18 08:30:00.000000', 'Trạng thái đơn hàng', 'ORDER_STATUS_CHANGED'),
('Đơn hàng #26 đã sẵn sàng để giao. Shipper hãy đến lấy hàng.', '2025-04-18 09:00:00.000000', 'Sẵn sàng giao hàng', 'NEW_ORDER_TO_SHIPPING');

-- Link notifications với shipper accounts
INSERT INTO `user_notification` (`is_deleted`, `is_read`, `notification_id`, `account_id`) VALUES
(b'0', b'0', 1, 103), -- Shipper 1
(b'0', b'1', 2, 103), -- Shipper 1  
(b'0', b'0', 3, 104); -- Shipper 2

-- ===================================
-- 8. CẬP NHẬT SHIPPER STATS
-- (Cập nhật thống kê cho shipper)
-- ===================================

-- Cập nhật stats cho shipper 1
UPDATE `shipper` SET 
    `total_orders` = 15,
    `completed_orders` = 12,
    `acceptance_rate` = 85.5,
    `cancellation_rate` = 2.1,
    `total_earnings` = 2100000,
    `gems` = 25,
    `rating` = 4.65
WHERE `id` = 1;

-- Cập nhật stats cho shipper 2  
UPDATE `shipper` SET
    `total_orders` = 12,
    `completed_orders` = 10,
    `acceptance_rate` = 90.0,
    `cancellation_rate` = 1.5,
    `total_earnings` = 1800000,
    `gems` = 18,
    `rating` = 4.75
WHERE `id` = 2;

-- ===================================
-- HOÀN THÀNH SCRIPT
-- ===================================

-- Kiểm tra dữ liệu đã insert
SELECT 'Order Assignments inserted:' as Info, COUNT(*) as Count FROM `order_assignment`;
SELECT 'Transactions inserted:' as Info, COUNT(*) as Count FROM `transaction`;  
SELECT 'Rewards inserted:' as Info, COUNT(*) as Count FROM `rewards`;
SELECT 'Wallets total:' as Info, COUNT(*) as Count FROM `wallet`;
SELECT 'Active Shippers:' as Info, COUNT(*) as Count FROM `shipper` WHERE `status` = 'ACTIVE';

COMMIT; 