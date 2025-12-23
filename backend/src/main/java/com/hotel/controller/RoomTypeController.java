package com.hotel.controller;

import com.hotel.common.Result;
import com.hotel.entity.RoomType;
import com.hotel.service.RoomTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/room-types")
@RequiredArgsConstructor
public class RoomTypeController {
    
    private final RoomTypeService roomTypeService;
    
    @GetMapping
    public Result<List<RoomType>> getAllRoomTypes() {
        return Result.success(roomTypeService.list());
    }
    
    @GetMapping("/{id}")
    public Result<RoomType> getRoomType(@PathVariable Long id) {
        return Result.success(roomTypeService.getById(id));
    }
}
