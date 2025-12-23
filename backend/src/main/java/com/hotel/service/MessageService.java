package com.hotel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hotel.entity.Message;
import java.util.List;

public interface MessageService extends IService<Message> {
    List<Message> getUserMessages(Long userId);
    void markAsRead(Long messageId);
    void sendMessage(Long userId, String title, String content, Integer type);
    int getUnreadCount(Long userId);
}
