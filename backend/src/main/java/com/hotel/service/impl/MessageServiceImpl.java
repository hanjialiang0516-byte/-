package com.hotel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hotel.entity.Message;
import com.hotel.mapper.MessageMapper;
import com.hotel.service.MessageService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {
    
    @Override
    public List<Message> getUserMessages(Long userId) {
        return list(new LambdaQueryWrapper<Message>()
                .eq(Message::getUserId, userId)
                .orderByDesc(Message::getCreateTime));
    }
    
    @Override
    public void markAsRead(Long messageId) {
        Message message = getById(messageId);
        if (message != null) {
            message.setIsRead(1);
            updateById(message);
        }
    }
    
    @Override
    public void sendMessage(Long userId, String title, String content, Integer type) {
        Message message = new Message();
        message.setUserId(userId);
        message.setTitle(title);
        message.setContent(content);
        message.setType(type);
        message.setIsRead(0);
        save(message);
    }
    
    @Override
    public int getUnreadCount(Long userId) {
        return (int) count(new LambdaQueryWrapper<Message>()
                .eq(Message::getUserId, userId)
                .eq(Message::getIsRead, 0));
    }
}
