package com.hotel.controller.admin;

import com.hotel.common.Result;
import com.hotel.entity.Room;
import com.hotel.entity.RoomType;
import com.hotel.service.RoomService;
import com.hotel.service.RoomTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/rooms")
@RequiredArgsConstructor
public class AdminRoomController {
    
    private final RoomService roomService;
    private final RoomTypeService roomTypeService;
    
    @GetMapping
    public Result<List<Room>> getAllRooms() {
        return Result.success(roomService.getAllRoomsWithStatus());
    }
    
    @PostMapping
    public Result<Room> createRoom(@RequestBody Room room) {
        roomService.save(room);
        return Result.success(room);
    }
    
    @PutMapping("/{id}")
    public Result<Void> updateRoom(@PathVariable Long id, @RequestBody Room room) {
        room.setId(id);
        roomService.updateById(room);
        return Result.success();
    }
    
    @PutMapping("/{id}/status")
    public Result<Void> updateRoomStatus(@PathVariable Long id, @RequestBody Room room) {
        roomService.updateRoomStatus(id, room.getStatus());
        return Result.success();
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> deleteRoom(@PathVariable Long id) {
        roomService.removeById(id);
        return Result.success();
    }
    
    @GetMapping("/types")
    public Result<List<RoomType>> getAllRoomTypes() {
        return Result.success(roomTypeService.list());
    }
    
    @PostMapping("/types")
    public Result<RoomType> createRoomType(@RequestBody RoomType roomType) {
        roomTypeService.save(roomType);
        return Result.success(roomType);
    }
    
    @PutMapping("/types/{id}")
    public Result<Void> updateRoomType(@PathVariable Long id, @RequestBody RoomType roomType) {
        roomType.setId(id);
        roomTypeService.updateById(roomType);
        return Result.success();
    }
    
    @DeleteMapping("/types/{id}")
    public Result<Void> deleteRoomType(@PathVariable Long id) {
        roomTypeService.removeById(id);
        return Result.success();
    }
}
