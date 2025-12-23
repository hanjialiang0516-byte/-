package com.hotel.controller;

import com.hotel.common.Result;
import com.hotel.entity.Room;
import com.hotel.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {
    
    private final RoomService roomService;
    
    @GetMapping("/available")
    public Result<List<Room>> getAvailableRooms(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut) {
        return Result.success(roomService.getAvailableRooms(checkIn, checkOut));
    }
    
    @GetMapping
    public Result<List<Room>> getAllRooms() {
        return Result.success(roomService.getAllRoomsWithStatus());
    }
}
