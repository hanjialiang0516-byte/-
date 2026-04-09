package com.hotel.dto;

import lombok.Data;
import java.time.LocalDate;

/**
 * 经营统计查询参数 DTO
 * 作用：接收前端传来的查询条件
 */
@Data
public class ReportQueryDTO {
    
    /** 开始日期 */
    private LocalDate startDate;
    
    /** 结束日期 */
    private LocalDate endDate;
    
    /** 房型 ID（可选） */
    private Long roomTypeId;
    
    /** 订单状态（可选） */
    private Integer status;
}