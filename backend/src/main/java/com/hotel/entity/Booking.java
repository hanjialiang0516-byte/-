package com.hotel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("booking")
public class Booking {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String orderNo; // 订单号
    private Long userId;
    private Long roomId; // 房间 ID
    private String guestName; // 预订人姓名
    private String guestPhone; // 预订人手机号
    private String guestIdCard; // 预订人身份证号
    private LocalDate checkInDate; // 入住日期
    private LocalDate checkOutDate;
    private BigDecimal totalPrice; // 总价
    private Integer status; // 0-待确认 1-已确认 2-已入住 3-已退房 4-已取消
    private Integer source; // 0-在线预订 1-电话预订 2-现场预订
    private String remark;
    private LocalDateTime payTime; // 支付时间
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableField(exist = false)
    private Room room;
    @TableField(exist = false)
    private User user;
}
