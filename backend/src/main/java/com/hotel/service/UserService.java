package com.hotel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hotel.entity.User;

public interface UserService extends IService<User> {
    User findByUsername(String username);
    String login(String username, String password);
    User register(User user);
    void updateProfile(User user);
}
