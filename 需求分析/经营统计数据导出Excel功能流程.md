# 经营统计数据导出 Excel 功能流程（修正版）

## 📋 功能概述

将酒店经营统计数据（订单明细）导出为 Excel 文件,供管理员下载分析。

---

## 🎯 核心概念纠正

### DTO vs VO vs Entity 的区别

| 类型 | 全称 | 作用 | 数据流向 | 示例 |
|------|------|------|---------|------|
| **DTO** | Data Transfer Object | **接收前端传来的参数** | 前端 → 后端 | `ReportQueryDTO`（时间范围、房型） |
| **VO** | View Object | **返回给前端/Excel 的数据** | 后端 → 前端 | `ReportExportVO`（Excel 列数据） |
| **Entity** | 实体类 | 映射数据库表 | 数据库 ↔ 后端 | `Booking`、`Room` |

**简单记忆：**
- **DTO**：**传进来**的参数（请求数据）
- **VO**：**传出去**的结果（响应数据）
- **Entity**：**数据库**的映射（持久化数据）

---

##  根据需求设计的数据结构

### 前端传来的参数（DTO）
- ✅ 时间范围（startDate、endDate）
- ✅ 房型筛选（roomTypeId）
- ✅ 订单状态（status）

### Excel 导出的列（VO）
根据你提供的截图：
- 支付时间
- 订单号
- 房间号
- 房型
- 入住人
- 入住天数
- 金额
- 入住日期

---

## ️ 实现流程

### 步骤 1：添加 Maven 依赖

在 `backend/pom.xml` 中添加 EasyExcel 依赖：

```xml
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>easyexcel</artifactId>
    <version>3.3.2</version>
</dependency>
```

**注意：** 添加后需要刷新 Maven 依赖

---

### 步骤 2：创建请求参数 DTO

**文件位置：** `backend/src/main/java/com/hotel/dto/ReportQueryDTO.java`

**作用：** 接收前端传来的查询参数（时间范围、房型等）

```java
package com.hotel.dto;

import lombok.Data;
import java.time.LocalDate;

/**
 * 经营统计查询参数 DTO
 * 用于接收前端传来的查询条件
 */
@Data
public class ReportQueryDTO {
    
    /**
     * 开始日期
     */
    private LocalDate startDate;
    
    /**
     * 结束日期
     */
    private LocalDate endDate;
    
    /**
     * 房型 ID（可选，为空则查询所有房型）
     */
    private Long roomTypeId;
    
    /**
     * 订单状态（可选）
     * 0=待确认，1=已确认，2=已入住，3=已完成，4=已取消
     */
    private Integer status;
}
```

---

### 步骤 3：创建 Excel 导出 VO

**文件位置：** `backend/src/main/java/com/hotel/vo/ReportExportVO.java`

**作用：** 定义 Excel 的列结构和数据格式

```java
package com.hotel.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 经营统计导出 VO
 * 用于 Excel 导出的数据模型
 */
@Data
public class ReportExportVO {
    
    @ExcelProperty(value = "支付时间", index = 0)
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime payTime;
    
    @ExcelProperty(value = "订单号", index = 1)
    private String orderNo;
    
    @ExcelProperty(value = "房间号", index = 2)
    private String roomNumber;
    
    @ExcelProperty(value = "房型", index = 3)
    private String roomTypeName;
    
    @ExcelProperty(value = "入住人", index = 4)
    private String guestName;
    
    @ExcelProperty(value = "入住天数", index = 5)
    private Integer stayDays;
    
    @ExcelProperty(value = "金额(元)", index = 6)
    private BigDecimal amount;
    
    @ExcelProperty(value = "入住日期", index = 7)
    @DateTimeFormat("yyyy-MM-dd")
    private LocalDateTime checkInDate;
}
```

**关键注解说明：**
- `@ExcelProperty(value = "列名", index = 序号)`：定义列名和顺序
- `@DateTimeFormat("yyyy-MM-dd HH:mm:ss")`：日期时间格式化
- `index` 从 0 开始，决定列的显示顺序

---

### 步骤 4：包结构说明

```
com.hotel/
├── dto/                    # 接收参数的数据传输对象
│   └── ReportQueryDTO.java # 查询参数（时间范围、房型等）
├── vo/                     # 返回给视图的数据对象
│   └── ReportExportVO.java # Excel 导出数据模型
└── entity/                 # 数据库实体类
    ├── Booking.java
    ├── Room.java
    └── RoomType.java
```

---

### 步骤 5：Service 层处理数据

**接口定义：** `backend/src/main/java/com/hotel/service/AdminReportService.java`

```java
public interface AdminReportService {
    /**
     * 获取经营统计数据（用于导出）
     * @param queryDTO 查询参数（时间范围、房型等）
     * @return Excel 导出数据列表
     */
    List<ReportExportVO> getReportDataForExport(ReportQueryDTO queryDTO);
}
```

**实现类：** `backend/src/main/java/com/hotel/service/impl/AdminReportServiceImpl.java`

```java
@Service
@RequiredArgsConstructor
public class AdminReportServiceImpl implements AdminReportService {
    
    private final BookingMapper bookingMapper;
    private final RoomMapper roomMapper;
    private final RoomTypeMapper roomTypeMapper;
    
    @Override
    public List<ReportExportVO> getReportDataForExport(ReportQueryDTO queryDTO) {
        // 1. 根据查询条件查询订单数据
        LambdaQueryWrapper<Booking> wrapper = new LambdaQueryWrapper<>();
        
        // 时间范围查询
        if (queryDTO.getStartDate() != null) {
            wrapper.ge(Booking::getPayTime, queryDTO.getStartDate().atStartOfDay());
        }
        if (queryDTO.getEndDate() != null) {
            wrapper.le(Booking::getPayTime, queryDTO.getEndDate().atTime(23, 59, 59));
        }
        
        // 房型筛选
        if (queryDTO.getRoomTypeId() != null) {
            wrapper.eq(Booking::getRoomTypeId, queryDTO.getRoomTypeId());
        }
        
        // 状态筛选
        if (queryDTO.getStatus() != null) {
            wrapper.eq(Booking::getStatus, queryDTO.getStatus());
        }
        
        // 按支付时间倒序
        wrapper.orderByDesc(Booking::getPayTime);
        
        List<Booking> bookings = bookingMapper.selectList(wrapper);
        
        // 2. 转换为 VO 列表
        List<ReportExportVO> voList = new ArrayList<>();
        
        for (Booking booking : bookings) {
            ReportExportVO vo = new ReportExportVO();
            
            // 支付时间
            vo.setPayTime(booking.getPayTime());
            
            // 订单号
            vo.setOrderNo(booking.getOrderNo());
            
            // 房间号（关联查询）
            Room room = roomMapper.selectById(booking.getRoomId());
            vo.setRoomNumber(room != null ? room.getRoomNumber() : "-");
            
            // 房型名称（关联查询）
            RoomType roomType = roomTypeMapper.selectById(booking.getRoomTypeId());
            vo.setRoomTypeName(roomType != null ? roomType.getName() : "-");
            
            // 入住人
            vo.setGuestName(booking.getGuestName());
            
            // 入住天数
            long days = ChronoUnit.DAYS.between(
                booking.getCheckInDate().toLocalDate(),
                booking.getCheckOutDate().toLocalDate()
            );
            vo.setStayDays((int) days);
            
            // 金额
            vo.setAmount(booking.getAmount());
            
            // 入住日期
            vo.setCheckInDate(booking.getCheckInDate());
            
            voList.add(vo);
        }
        
        return voList;
    }
}
```

---

### 步骤 6：Controller 层提供导出接口

**文件位置：** `backend/src/main/java/com/hotel/controller/admin/AdminReportController.java`

```java
@RestController
@RequestMapping("/api/admin/reports")
@RequiredArgsConstructor
public class AdminReportController {
    
    private final AdminReportService reportService;
    
    /**
     * 导出经营统计数据为 Excel
     */
    @GetMapping("/export")
    public void exportReport(
            @ModelAttribute ReportQueryDTO queryDTO,
            HttpServletResponse response) throws IOException {
        
        // 1. 设置默认时间范围（最近30天）
        if (queryDTO.getStartDate() == null) {
            queryDTO.setStartDate(LocalDate.now().minusDays(30));
        }
        if (queryDTO.getEndDate() == null) {
            queryDTO.setEndDate(LocalDate.now());
        }
        
        // 2. 查询数据（传入 DTO）
        List<ReportExportVO> voList = reportService.getReportDataForExport(queryDTO);
        
        // 3. 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        
        // 文件名编码（支持中文）
        String fileName = URLEncoder.encode("经营统计_" + LocalDate.now(), "UTF-8")
                .replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", 
            "attachment;filename*=utf-8''" + fileName + ".xlsx");
        
        // 4. 写入 Excel（使用 VO）
        EasyExcel.write(response.getOutputStream(), ReportExportVO.class)
            .sheet("经营统计")
            .doWrite(voList);
    }
}
```

**关键点说明：**
- 使用 `@ModelAttribute` 接收 DTO 参数
- 返回类型是 `void`（直接写入响应流）
- Content-Type 必须设置为 Excel 格式
- 文件名需要 URL 编码
- 使用 `EasyExcel.write()` 直接写入响应流

---

### 步骤 7：前端调用导出接口

#### 推荐方式（axios + blob）

**API 定义：** `frontend/src/api/index.js`

```javascript
export const reportApi = {
  exportReport: async (params) => {
    const response = await api.get('/admin/reports/export', {
      params,  // { startDate, endDate, roomTypeId, status }
      responseType: 'blob'  // 重要：指定响应类型为二进制
    })
    
    // 创建下载链接
    const url = window.URL.createObjectURL(new Blob([response.data]))
    const link = document.createElement('a')
    link.href = url
    
    // 从响应头获取文件名
    const contentDisposition = response.headers['content-disposition']
    let fileName = '经营统计.xlsx'
    if (contentDisposition) {
      const match = contentDisposition.match(/filename\*=(?:UTF-8'')?([^;]+)/)
      if (match) {
        fileName = decodeURIComponent(match[1])
      }
    }
    
    link.setAttribute('download', fileName)
    document.body.appendChild(link)
    link.click()
    
    // 清理
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
  }
}
```

**Vue 组件调用：** `frontend/src/views/admin/Reports.vue`

```vue
<template>
  <div class="report-container">
    <!-- 筛选条件 -->
    <el-form :inline="true" :model="queryForm">
      <el-form-item label="时间范围">
        <el-date-picker
          v-model="queryForm.dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
        />
      </el-form-item>
      
      <el-form-item label="房型">
        <el-select v-model="queryForm.roomTypeId" placeholder="全部房型" clearable>
          <el-option
            v-for="type in roomTypes"
            :key="type.id"
            :label="type.name"
            :value="type.id"
          />
        </el-select>
      </el-form-item>
      
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button type="success" @click="handleExport" :loading="exporting">
          <el-icon><Download /></el-icon>
          导出 Excel
        </el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { Download } from '@element-plus/icons-vue'
import { reportApi } from '@/api'

const queryForm = reactive({
  dateRange: [],
  roomTypeId: null,
  status: null
})

const exporting = ref(false)

const handleExport = async () => {
  if (!queryForm.dateRange || queryForm.dateRange.length !== 2) {
    ElMessage.warning('请选择时间范围')
    return
  }
  
  exporting.value = true
  try {
    await reportApi.exportReport({
      startDate: queryForm.dateRange[0],
      endDate: queryForm.dateRange[1],
      roomTypeId: queryForm.roomTypeId,
      status: queryForm.status
    })
    ElMessage.success('导出成功')
  } catch (error) {
    console.error(error)
    ElMessage.error('导出失败')
  } finally {
    exporting.value = false
  }
}
</script>
```

---

## 📊 完整流程图

```
┌─────────────────┐
│ 用户选择筛选条件 │
│ - 时间范围       │
│ - 房型          │
│ - 订单状态       │
└────────┬────────┘
         │
         ▼
┌─────────────────────────┐
│ 前端封装为 DTO          │
│ {                       │
│   startDate,            │
│   endDate,              │
│   roomTypeId,           │
│   status                │
│ }                       │
└────────┬────────────────┘
         │
         ▼
┌─────────────────────────┐
│ GET /api/admin/reports  │
│ /export?startDate=...   │
│ &endDate=...            │
│ &roomTypeId=...         │
└────────┬────────────────┘
         │
         ▼
┌─────────────────────────┐
│ Controller 接收 DTO     │
│ @ModelAttribute         │
│ ReportQueryDTO          │
└────────┬────────────────┘
         │
         ▼
┌─────────────────────────┐
│ Service 根据 DTO 条件   │
│ 查询数据库（Entity）    │
└────────┬────────────────┘
         │
         ▼
┌─────────────────────────┐
│ 将 Entity 转换为 VO     │
│ List<ReportExportVO>    │
└────────┬────────────────┘
         │
         ▼
┌─────────────────────────┐
│ EasyExcel 使用 VO       │
│ 生成 Excel 文件         │
└────────┬────────────────
         │
         ▼
┌─────────────────────────┐
│ 写入 Response 输出流    │
└────────┬────────────────
         │
         ▼
┌─────────────────────────┐
│ 浏览器自动下载          │
│ Excel 文件              │
└────────┬────────────────
         │
         ▼
┌─────────────────────────┐
│ 用户打开查看            │
└─────────────────────────┘
```

---

##  数据流转说明

### 数据流向图

```
前端请求参数（JSON）
    ↓
DTO（ReportQueryDTO）
    ↓ 传入
Service 层
    ↓ 查询
Entity（Booking、Room、RoomType）
    ↓ 转换
VO（ReportExportVO）
    ↓ 导出
Excel 文件
    ↓ 下载
前端接收文件
```

---

## 📝 Excel 文件内容示例

根据你提供的截图：

| 支付时间 | 订单号 | 房间号 | 房型 | 入住人 | 入住天数 | 金额(元) | 入住日期 |
|---------|--------|--------|------|--------|---------|----------|----------|
| 2024-01-15 10:30:00 | ORD20240115001 | 801 | 豪华大床房 | 张三 | 3 | 897.00 | 2024-01-15 |
| 2024-01-15 14:20:00 | ORD20240115002 | 605 | 标准双床房 | 李四 | 2 | 598.00 | 2024-01-16 |
| 2024-01-16 09:15:00 | ORD20240116001 | 1002 | 商务套房 | 王五 | 5 | 2495.00 | 2024-01-16 |

---

## ⚠️ 注意事项

### 1. DTO 和 VO 的使用场景

| 场景 | 使用 | 原因 |
|------|------|------|
| 接收前端参数 | **DTO** | 数据传输对象，负责接收输入 |
| 返回给前端/Excel | **VO** | 视图对象，负责输出展示 |
| 数据库操作 | **Entity** | 实体类，映射数据库表 |

### 2. 为什么不能混用？

**❌ 错误做法：用 DTO 接收又返回**
```java
// 问题：DTO 包含了不应该暴露的字段
public ReportDTO getReport(ReportDTO queryDTO) { ... }
```

**✅ 正确做法：DTO 接收，VO 返回**
```java
// 清晰：输入和输出分离
public List<ReportExportVO> getReport(ReportQueryDTO queryDTO) { ... }
```

### 3. 权限控制

```java
@PreAuthorize("hasRole('ADMIN')")  // 只有管理员可导出
@GetMapping("/export")
public void exportReport(...) { ... }
```

### 4. 数据量限制

```java
if (voList.size() > 100000) {
    throw new RuntimeException("数据量过大，请缩小时间范围");
}
```

### 5. 文件名乱码问题

```java
String fileName = URLEncoder.encode("经营统计", "UTF-8")
    .replaceAll("\\+", "%20");
response.setHeader("Content-disposition", 
    "attachment;filename*=utf-8''" + fileName + ".xlsx");
```

---

## 🎯 总结

### 核心步骤
1. ✅ 添加 EasyExcel 依赖
2. ✅ 创建 **DTO** 接收查询参数（时间范围、房型等）
3. ✅ 创建 **VO** 定义 Excel 列结构
4. ✅ Service 层查询数据并转换为 VO 列表
5. ✅ Controller 接收 DTO，写入 Excel 响应流
6. ✅ 前端触发下载

### 关键概念
- **DTO（Data Transfer Object）**：用于**接收参数**，从前端传到后端
- **VO（View Object）**：用于**返回数据**，从后端返回给前端/Excel
- **Entity**：用于**映射数据库表**，与数据库交互

### 数据流向
```
前端 → DTO → Service → Entity → VO → Excel → 前端下载
```

---

## 🔗 相关文件清单

```
backend/
├── pom.xml                                    # Maven 依赖
├── src/main/java/com/hotel/
│   ├── dto/                                   # 接收参数的数据传输对象
│   │   └── ReportQueryDTO.java               # 查询参数（时间范围、房型等）
│   ├── vo/                                    # 返回给视图的数据对象
│   │   └── ReportExportVO.java               # Excel 导出数据模型
│   ├── service/
│   │   ├── AdminReportService.java           # 服务接口
│   │   └── impl/
│   │       └── AdminReportServiceImpl.java   # 服务实现
│   └── controller/admin/
│       └── AdminReportController.java        # 控制器

frontend/
├── src/
│   ├── api/
│   │   └── index.js                          # API 定义
│   └── views/admin/
│       └── Reports.vue                       # 报表页面
```

---

**文档版本：** v2.0（修正版）  
**最后更新：** 2024-01-XX  
**修正说明：** 纠正了 DTO 和 VO 的概念混淆
- DTO 用于接收参数（时间范围、房型等）
- VO 用于导出数据（支付时间、订单号、房间号等）
- Entity 用于数据库映射
