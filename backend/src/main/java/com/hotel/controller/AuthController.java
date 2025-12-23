package com.hotel.controller;

import com.hotel.common.Result;
import com.hotel.entity.User;
import com.hotel.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final UserService userService;
    
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> params) {
        String token = userService.login(params.get("username"), params.get("password"));
        User user = userService.findByUsername(params.get("username"));
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
