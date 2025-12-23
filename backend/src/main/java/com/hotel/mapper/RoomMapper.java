package com.hotel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hotel.entity.Room;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface RoomMapper extends BaseMapper<Room> {
    @Select("SELECT r.*, rt.name as room_type_name, rt.price FROM room r " +
            "LEFT JOIN room_type rt ON r.room_type_id = rt.id")
    List<Room> selectAllWithType();
}
