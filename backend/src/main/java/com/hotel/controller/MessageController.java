package com.hotel.controller;

import com.hotel.common.Result;
import com.hotel.entity.Message;
import com.hotel.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {
    
    private final MessageService messageService;
    
    @GetMapping
    public Result<List<Message>> getMessages(Authentication auth) {
        return Result.success(messageService.getUserMessages((Long) auth.getPrincipal()));
    }
    
    @GetMapping("/unread-count")
    public Result<Map<String, Integer>> getUnreadCount(Authentication auth) {
        return Result.success(Map.of("count", messageService.getUnreadCount((Long) auth.getPrincipal())));
    }
    
    @PostMapping("/{id}/read")
    public Result<Void> markAsRead(@PathVariable Long id) {
        messageService.markAsRead(id);
        return Result.success();
    }
}
