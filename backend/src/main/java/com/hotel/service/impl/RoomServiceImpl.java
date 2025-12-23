package com.hotel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hotel.entity.Room;
import com.hotel.entity.RoomType;
import com.hotel.mapper.RoomMapper;
import com.hotel.mapper.RoomTypeMapper;
import com.hotel.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl extends ServiceImpl<RoomMapper, Room> implements RoomService {
    
    private final RoomTypeMapper roomTypeMapper;
    
    @Override
    public List<Room> getAvailableRooms(LocalDate checkIn, LocalDate checkOut) {
        List<Room> rooms = list(new LambdaQueryWrapper<Room>().eq(Room::getStatus, 0));
        rooms.forEach(room -> room.setRoomType(roomTypeMapper.selectById(room.getRoomTypeId())));
        return rooms;
    }
    
    @Override
    public List<Room> getAllRoomsWithStatus() {
        List<Room> rooms = list();
        rooms.forEach(room -> room.setRoomType(roomTypeMapper.selectById(room.getRoomTypeId())));
        return rooms;
    }
    
    @Override
    public void updateRoomStatus(Long roomId, Integer status) {
        Room room = getById(roomId);
        if (room != null) {
            room.setStatus(status);
            updateById(room);
        }
    }
}
