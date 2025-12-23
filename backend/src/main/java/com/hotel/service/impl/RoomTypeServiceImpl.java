package com.hotel.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hotel.entity.RoomType;
import com.hotel.mapper.RoomTypeMapper;
import com.hotel.service.RoomTypeService;
import org.springframework.stereotype.Service;

@Service
public class RoomTypeServiceImpl extends ServiceImpl<RoomTypeMapper, RoomType> implements RoomTypeService {
}
