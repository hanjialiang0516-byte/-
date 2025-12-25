-- 酒店预订系统数据库初始化脚本
CREATE DATABASE IF NOT EXISTS hotel_booking DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE hotel_booking;

-- 用户表
CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    real_name VARCHAR(50) COMMENT '真实姓名',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    avatar MEDIUMTEXT COMMENT '头像(base64)',
    role TINYINT DEFAULT 0 COMMENT '角色：0-普通用户 1-管理员',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT '用户表';

-- 房型表
CREATE TABLE room_type (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL COMMENT '房型名称',
    description TEXT COMMENT '房型描述',
    price DECIMAL(10,2) NOT NULL COMMENT '基础价格',
    capacity INT DEFAULT 2 COMMENT '可住人数',
    bed_type VARCHAR(50) COMMENT '床型',
    area DECIMAL(6,2) COMMENT '面积(平方米)',
    amenities VARCHAR(500) COMMENT '设施(JSON)',
    images VARCHAR(1000) COMMENT '图片(JSON)',
    status TINYINT DEFAULT 1 COMMENT '状态：0-下架 1-上架',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT '房型表';

-- 房间表
CREATE TABLE room (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    room_number VARCHAR(20) NOT NULL UNIQUE COMMENT '房间号',
    room_type_id BIGINT NOT NULL COMMENT '房型ID',
    floor INT COMMENT '楼层',
    status TINYINT DEFAULT 0 COMMENT '状态：0-空闲 1-已预订 2-入住中 3-清洁中 4-维修中',
    remark VARCHAR(255) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (room_type_id) REFERENCES room_type(id)
) COMMENT '房间表';

-- 预订订单表
CREATE TABLE booking (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_no VARCHAR(32) NOT NULL UNIQUE COMMENT '订单号',
    user_id BIGINT COMMENT '用户ID',
    room_id BIGINT NOT NULL COMMENT '房间ID',
    guest_name VARCHAR(50) NOT NULL COMMENT '入住人姓名',
    guest_phone VARCHAR(20) NOT NULL COMMENT '入住人电话',
    guest_id_card VARCHAR(20) COMMENT '身份证号',
    check_in_date DATE NOT NULL COMMENT '入住日期',
    check_out_date DATE NOT NULL COMMENT '离店日期',
    total_price DECIMAL(10,2) NOT NULL COMMENT '总价',
    status TINYINT DEFAULT 0 COMMENT '状态：0-待确认 1-已确认 2-已入住 3-已退房 4-已取消',
    source TINYINT DEFAULT 0 COMMENT '来源：0-在线预订 1-电话预订 2-现场预订',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES sys_user(id),
    FOREIGN KEY (room_id) REFERENCES room(id)
) COMMENT '预订订单表';

-- 消息通知表
CREATE TABLE message (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    title VARCHAR(100) NOT NULL COMMENT '标题',
    content TEXT COMMENT '内容',
    type TINYINT DEFAULT 0 COMMENT '类型：0-系统通知 1-预订提醒 2-促销信息',
    is_read TINYINT DEFAULT 0 COMMENT '是否已读：0-未读 1-已读',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES sys_user(id)
) COMMENT '消息通知表';

-- 系统配置表
CREATE TABLE sys_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    config_key VARCHAR(100) NOT NULL UNIQUE COMMENT '配置键',
    config_value TEXT COMMENT '配置值',
    description VARCHAR(255) COMMENT '描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT '系统配置表';

-- 初始化管理员账号 (密码: admin123)
INSERT INTO sys_user (username, password, real_name, role, status) 
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '系统管理员', 1, 1);
