package com.hotel.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
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
    private LocalDate checkInDate;
}