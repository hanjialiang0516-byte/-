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
    private String orderNo;
    private Long userId;
    private Long roomId;
    private String guestName;
    private String guestPhone;
    private String guestIdCard;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private BigDecimal totalPrice;
    private Integer status; // 0-待确认 1-已确认 2-已入住 3-已退房 4-已取消
    private Integer source; // 0-在线预订 1-电话预订 2-现场预订
    private String remark;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableField(exist = false)
    private Room room;
    @TableField(exist = false)
    private User user;
}
