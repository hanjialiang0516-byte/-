package com.hotel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("room")
public class Room {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String roomNumber;
    private Long roomTypeId;
    private Integer floor;
    private Integer status; // 0-空闲 1-已预订 2-入住中 3-清洁中 4-维修中
    private String remark;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableField(exist = false)
    private RoomType roomType;
}
