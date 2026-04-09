package com.hotel.controller;

import com.hotel.common.Result;
import com.hotel.entity.User;
import com.hotel.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    
    private final UserService userService;
    
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> params) {
        log.info("Login: {}", params);
        String token = userService.login(params.get("username"), params.get("password"));
        User user = userService.findByUsername(params.get("username")); // 获取用户信息
        user.setPassword(null);
        return Result.success(Map.of("token", token, "user", user));
    }

    @PostMapping("/register")
    public Result<User> register(@RequestBody User user) {
        User registered = userService.register(user);
        registered.setPassword(null);
        return Result.success(registered);
    }
}
