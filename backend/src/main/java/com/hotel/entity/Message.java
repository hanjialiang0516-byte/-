package com.hotel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("message")
public class Message {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private Integer type; // 0-系统通知 1-预订提醒 2-促销信息
    private Integer isRead; // 0-未读 1-已读
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
