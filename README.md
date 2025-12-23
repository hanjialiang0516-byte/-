# 酒店预订系统

基于 Spring Boot + Vue 3 + MySQL 的酒店预订管理系统。

## 技术栈

- 后端：Spring Boot 3.2 + MyBatis-Plus + Spring Security + JWT
- 前端：Vue 3 + Vite + Element Plus + Pinia
- 数据库：MySQL 8.0

## 功能模块

### 用户端
- 实时房态查看
- 一键快捷预订
- 我的预订中心
- 个人资料管理
- 消息与提醒

### 管理端
- 房态总控面板
- 预订订单管理
- 客房资源管理
- 客户档案管理
- 电话预订登记

## 快速开始

### 1. 数据库初始化
```sql
-- 执行 sql/init.sql 创建数据库和表
mysql -u root -p < sql/init.sql
```

### 2. 后端配置
修改 `backend/src/main/resources/application.yml` 中的数据库连接信息：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/hotel_booking
    username: root
    password: your_password
```

### 3. 启动后端
```bash
cd backend
mvn spring-boot:run
```

### 4. 启动前端
```bash
cd frontend
npm install
npm run dev
```

### 5. 访问系统
- 前台：http://localhost:5173
- 默认管理员：admin / admin123

## 项目结构

```
hotel-booking-system/
├── backend/                 # Spring Boot 后端
│   └── src/main/java/com/hotel/
│       ├── controller/      # 控制器
│       ├── service/         # 业务逻辑
│       ├── mapper/          # 数据访问
│       ├── entity/          # 实体类
│       ├── config/          # 配置类
│       └── util/            # 工具类
├── frontend/                # Vue 前端
│   └── src/
│       ├── views/           # 页面组件
│       ├── api/             # API接口
│       ├── store/           # 状态管理
│       └── router/          # 路由配置
└── sql/                     # 数据库脚本
```
