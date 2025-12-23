-- 酒店预订系统测试数据
USE hotel_booking;

-- 清空旧数据
DELETE FROM message;
DELETE FROM booking;
DELETE FROM room;
DELETE FROM room_type;
DELETE FROM sys_user WHERE username != 'admin';

-- 更新管理员密码为 password
UPDATE sys_user SET password = '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2' WHERE username = 'admin';

-- 添加测试用户 (密码都是 password)
INSERT INTO sys_user (username, password, real_name, phone, email, role, status) VALUES 
('user1', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '张三', '13800138001', 'zhangsan@test.com', 0, 1),
('user2', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '李四', '13800138002', 'lisi@test.com', 0, 1),
('user3', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '王五', '13800138003', 'wangwu@test.com', 0, 1),
('user4', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '赵六', '13800138004', 'zhaoliu@test.com', 0, 1);

-- 添加房型
INSERT INTO room_type (name, description, price, capacity, bed_type, area, status) VALUES 
('经济单人间', '简约舒适，适合商务出行，配备独立卫浴、空调、免费WiFi', 168.00, 1, '1.2米单人床', 18, 1),
('标准双人间', '温馨双人房，城市景观，配备独立卫浴、空调、液晶电视', 268.00, 2, '1.5米双人床', 25, 1),
('豪华大床房', '高档装修，配备浴缸、迷你吧、保险箱，享受尊贵体验', 388.00, 2, '1.8米大床', 35, 1),
('家庭亲子房', '宽敞空间，适合家庭入住，配备儿童设施和游戏区', 458.00, 4, '1.8米+1.2米床', 45, 1),
('总统套房', '顶级奢华，独立客厅书房，270度全景落地窗，管家服务', 1288.00, 2, '2米特大床', 80, 1);

-- 添加房间 (每层6间)
-- 1楼 经济单人间
INSERT INTO room (room_number, room_type_id, floor, status, remark) VALUES 
('101', 1, 1, 0, '靠近电梯'),
('102', 1, 1, 0, NULL),
('103', 1, 1, 2, '有客人入住'),
('104', 1, 1, 0, NULL),
('105', 1, 1, 1, '已被预订'),
('106', 1, 1, 0, '安静房间');

-- 2楼 标准双人间
INSERT INTO room (room_number, room_type_id, floor, status, remark) VALUES 
('201', 2, 2, 0, NULL),
('202', 2, 2, 2, '有客人入住'),
('203', 2, 2, 0, '窗户朝南'),
('204', 2, 2, 0, NULL),
('205', 2, 2, 3, '正在清洁'),
('206', 2, 2, 0, NULL);

-- 3楼 豪华大床房
INSERT INTO room (room_number, room_type_id, floor, status, remark) VALUES 
('301', 3, 3, 0, NULL),
('302', 3, 3, 0, NULL),
('303', 3, 3, 2, '有客人入住'),
('304', 3, 3, 0, '带浴缸'),
('305', 3, 3, 0, NULL),
('306', 3, 3, 4, '维修中');

-- 4楼 家庭亲子房
INSERT INTO room (room_number, room_type_id, floor, status, remark) VALUES 
('401', 4, 4, 0, '带儿童设施'),
('402', 4, 4, 0, NULL),
('403', 4, 4, 1, '已被预订'),
('404', 4, 4, 0, NULL),
('405', 4, 4, 0, '带游戏区'),
('406', 4, 4, 0, NULL);

-- 5楼 总统套房
INSERT INTO room (room_number, room_type_id, floor, status, remark) VALUES 
('501', 5, 5, 0, '顶层景观'),
('502', 5, 5, 2, '有客人入住'),
('503', 5, 5, 0, '270度全景'),
('504', 5, 5, 0, NULL),
('505', 5, 5, 0, '带私人泳池'),
('506', 5, 5, 4, '装修升级中');

-- 添加预订记录
INSERT INTO booking (order_no, user_id, room_id, guest_name, guest_phone, guest_id_card, check_in_date, check_out_date, total_price, status, source, remark) VALUES 
('202312230001', 2, 3, '张三', '13800138001', '110101199001011234', '2025-12-23', '2025-12-25', 336.00, 2, 0, '需要安静房间'),
('202312230002', 3, 8, '李四', '13800138002', '110101199002022345', '2025-12-23', '2025-12-26', 804.00, 2, 0, NULL),
('202312230003', 4, 15, '王五', '13800138003', '110101199003033456', '2025-12-23', '2025-12-25', 776.00, 2, 0, NULL),
('202312230004', 5, 26, '赵六', '13800138004', '110101199004044567', '2025-12-23', '2025-12-27', 5152.00, 2, 0, 'VIP客户'),
('202312230005', 2, 5, '张三', '13800138001', '110101199001011234', '2025-12-26', '2025-12-28', 336.00, 1, 0, NULL),
('202312230006', 3, 21, '李四', '13800138002', '110101199002022345', '2025-12-27', '2025-12-29', 916.00, 1, 1, '电话预订'),
('202312230007', 2, 1, '张三', '13800138001', '110101199001011234', '2025-12-18', '2025-12-20', 336.00, 3, 0, '已退房'),
('202312230008', 4, 7, '王五', '13800138003', '110101199003033456', '2025-12-15', '2025-12-18', 804.00, 3, 0, '已退房');

-- 添加消息通知
INSERT INTO message (user_id, title, content, type, is_read) VALUES 
(2, '预订成功', '您的订单 202312230001 已提交成功，我们将尽快为您确认。', 1, 1),
(2, '订单已确认', '您的订单 202312230001 已确认，请于2025-12-23 14:00后办理入住。', 1, 0),
(2, '入住提醒', '您预订的房间明天即将入住，请携带有效身份证件办理入住手续。', 1, 0),
(3, '预订成功', '您的订单 202312230002 已提交成功，感谢您的预订！', 1, 0),
(4, '预订成功', '您的订单 202312230003 已提交成功。', 1, 1),
(5, 'VIP专属优惠', '尊敬的VIP客户，您本次入住可享受免费早餐和延迟退房服务。', 2, 0),
(2, '圣诞特惠', '🎄圣诞节期间预订享8折优惠！使用优惠码XMAS2025立减100元！', 2, 0),
(3, '新年活动', '🎉新年特惠活动开始啦！连住3晚享7折优惠，快来预订吧！', 2, 0),
(2, '系统通知', '欢迎注册星辰酒店预订系统，祝您入住愉快！', 0, 1),
(3, '系统通知', '您的账户资料已更新成功。', 0, 1),
(4, '积分到账', '恭喜您获得500积分奖励，可在下次预订时抵扣现金！', 0, 0);

-- 添加系统配置
INSERT INTO sys_config (config_key, config_value, description) VALUES 
('hotel_name', '星辰酒店', '酒店名称'),
('hotel_phone', '400-888-8888', '预订热线'),
('hotel_address', '北京市朝阳区星辰大道88号', '酒店地址'),
('check_in_time', '14:00', '入住时间'),
('check_out_time', '12:00', '退房时间'),
('hotel_email', 'service@starhotel.com', '酒店邮箱');
