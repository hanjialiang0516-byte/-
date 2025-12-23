package com.hotel.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hotel.common.Result;
import com.hotel.entity.User;
import com.hotel.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {
    
    private final UserService userService;
    
    @GetMapping
    public Result<IPage<User>> getAllUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        IPage<User> users = userService.page(new Page<>(page, size));
        users.getRecords().forEach(u -> u.setPassword(null));
        return Result.success(users);
    }
    
    @PutMapping("/{id}/status")
    public Result<Void> updateUserStatus(@PathVariable Long id, @RequestBody User user) {
        User existing = userService.getById(id);
        existing.setStatus(user.getStatus());
        userService.updateById(existing);
        return Result.success();
    }
}
