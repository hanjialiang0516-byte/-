package com.hotel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hotel.entity.Room;
import java.time.LocalDate;
import java.util.List;

public interface RoomService extends IService<Room> {
    List<Room> getAvailableRooms(LocalDate checkIn, LocalDate checkOut);
    List<Room> getAllRoomsWithStatus();
    void updateRoomStatus(Long roomId, Integer status);
}
