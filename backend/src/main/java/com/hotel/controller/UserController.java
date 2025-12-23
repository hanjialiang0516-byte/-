package com.hotel.controller;

import com.hotel.common.Result;
import com.hotel.entity.User;
import com.hotel.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    @GetMapping("/profile")
    public Result<User> getProfile(Authentication auth) {
        User user = userService.getById((Long) auth.getPrincipal());
        user.setPassword(null);
        return Result.success(user);
    }
    
    @PutMapping("/profile")
    public Result<Void> updateProfile(@RequestBody User user, Authentication auth) {
        user.setId((Long) auth.getPrincipal());
        userService.updateProfile(user);
        return Result.success();
    }
}
