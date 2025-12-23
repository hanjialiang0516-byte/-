# 🏨 星辰酒店预订系统

基于 Spring Boot + Vue3 + MySQL 的酒店预订管理系统，适合作为课程设计项目。

## 📋 功能介绍

### 用户端
- 首页展示、客房浏览
- 在线预订、模拟支付
- 我的订单、个人中心
- 消息通知

### 管理端
- 房态总控面板
- 订单管理（确认、入住、退房）
- 客房管理（房间、房型）
- 客户档案管理
- 经营统计（营收流水）
- 数据报表

## 🛠️ 环境要求

| 软件 | 版本要求 | 下载地址 |
|------|---------|---------|
| JDK | 17+ | https://www.oracle.com/java/technologies/downloads/ |
| MySQL | 5.7+ | https://dev.mysql.com/downloads/mysql/ |
| Node.js | 16+ | https://nodejs.org/ |
| IntelliJ IDEA | 任意版本 | https://www.jetbrains.com/idea/download/ |

---

## 🚀 详细配置步骤

### 第一步：下载项目

```bash
git clone https://gitee.com/liang-hanjia/hotel.git
```

或者直接在 Gitee 页面点击"下载ZIP"解压。

---

### 第二步：配置数据库

#### 2.1 安装 MySQL（如已安装跳过）
下载安装 MySQL，记住设置的 root 密码。

#### 2.2 创建数据库
打开 Navicat（或其他数据库工具），连接 MySQL，然后：

1. **新建数据库**
   - 数据库名：`hotel_booking`
   - 字符集：`utf8mb4`

2. **执行初始化脚本**
   - 打开 `sql/init.sql` 文件，复制全部内容
   - 在 Navicat 中新建查询，粘贴并执行

3. **执行测试数据脚本**
   - 打开 `sql/test-data.sql` 文件，复制全部内容
   - 在 Navicat 中新建查询，粘贴并执行

#### 2.3 修改数据库密码
打开 `backend/src/main/resources/application.yml`，修改第 8 行的密码为你的 MySQL 密码：

```yaml
password: 你的MySQL密码
```

---

### 第三步：启动后端

1. 用 **IntelliJ IDEA** 打开 `backend` 文件夹
2. 等待 Maven 自动下载依赖（右下角会显示进度，首次可能需要几分钟）
3. 找到 `src/main/java/com/hotel/HotelApplication.java`
4. 右键点击，选择 **Run 'HotelApplication'**
5. 看到 `Started HotelApplication` 表示启动成功
6. 后端运行在 `http://localhost:8080`

---

### 第四步：启动前端

#### 4.1 检查 Node.js
打开命令行（cmd），输入：
```bash
node -v
```
如果显示版本号（如 v18.17.0）说明已安装。
如果提示"不是内部命令"，请先安装 Node.js。

#### 4.2 进入前端目录
方法一：在文件夹地址栏输入 `cmd` 回车
1. 打开项目文件夹
2. 进入 `frontend` 文件夹
3. 在地址栏输入 `cmd`，按回车

方法二：使用 cd 命令
```bash
cd 项目路径\frontend
```

#### 4.3 安装依赖并启动
```bash
npm install
npm run dev
```

看到类似以下输出表示成功：
```
VITE v5.x.x  ready in xxx ms
➜  Local:   http://localhost:5173/
```

---

### 第五步：访问系统

| 页面 | 地址 |
|------|------|
| 用户端首页 | http://localhost:5173 |
| 用户登录 | http://localhost:5173/login |
| 管理后台 | http://localhost:5173/admin |

### 测试账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | password |
| 普通用户 | user1 | password |

---

## ❓ 常见问题

### Q: 后端启动失败，提示数据库连接错误
A: 检查 MySQL 是否启动，数据库名和密码是否正确。

### Q: 前端 npm install 很慢
A: 使用淘宝镜像：
```bash
npm config set registry https://registry.npmmirror.com
npm install
```

### Q: 登录提示用户名或密码错误
A: 确保执行了 `test-data.sql`，密码是 `password`（不是 admin123）。

### Q: 端口被占用
A: 后端默认 8080，前端默认 5173。如果被占用，关闭占用程序或修改端口。

---

## 📁 项目结构

```
hotel-booking-system/
├── backend/                # 后端 Spring Boot 项目
│   ├── src/main/java/     # Java 源码
│   └── src/main/resources/ # 配置文件
├── frontend/              # 前端 Vue3 项目
│   ├── src/views/         # 页面组件
│   └── src/api/           # API 接口
└── sql/                   # 数据库脚本
    ├── init.sql           # 初始化脚本
    └── test-data.sql      # 测试数据
```

## 🔧 技术栈

- **后端**: Spring Boot 3.2 + MyBatis-Plus + Spring Security + JWT
- **前端**: Vue 3 + Vite + Element Plus + Pinia
- **数据库**: MySQL 8.0

---

如有问题，请联系项目作者。
