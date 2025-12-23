package com.hotel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hotel.entity.Booking;
import com.hotel.entity.Room;
import com.hotel.mapper.BookingMapper;
import com.hotel.service.BookingService;
import com.hotel.service.MessageService;
import com.hotel.service.RoomService;
import cn.hutool.core.util.IdUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl extends ServiceImpl<BookingMapper, Booking> implements BookingService {
    
    private final RoomService roomService;
    private final MessageService messageService;
    
    @Override
    @Transactional
    public Booking createBooking(Booking booking) {
        booking.setOrderNo(IdUtil.getSnowflakeNextIdStr());
        booking.setStatus(0); // 待支付
        save(booking);
        roomService.updateRoomStatus(booking.getRoomId(), 1);
        return booking;
    }
    
    @Override
    public Booking getBookingDetail(Long bookingId) {
        Booking booking = getById(bookingId);
        if (booking != null) {
            Room room = roomService.getById(booking.getRoomId());
            booking.setRoom(room);
        }
        return booking;
    }
    
    @Override
    @Transactional
    public void payBooking(Long bookingId) {
        Booking booking = getById(bookingId);
        if (booking != null && booking.getStatus() == 0) {
            booking.setStatus(1); // 已支付/已确认
            updateById(booking);
            if (booking.getUserId() != null) {
                messageService.sendMessage(booking.getUserId(), "支付成功", 
                    "您的订单 " + booking.getOrderNo() + " 已支付成功，请于入住日期14:00后办理入住", 1);
            }
        }
    }
    
    @Override
    @Transactional
    public void cancelBooking(Long bookingId) {
        Booking booking = getById(bookingId);
        if (booking != null && booking.getStatus() < 2) {
            booking.setStatus(4);
            updateById(booking);
            roomService.updateRoomStatus(booking.getRoomId(), 0);
            if (booking.getUserId() != null) {
                messageService.sendMessage(booking.getUserId(), "订单已取消", 
                    "您的订单 " + booking.getOrderNo() + " 已取消", 1);
            }
        }
    }
    
    @Override
    public void confirmBooking(Long bookingId) {
        Booking booking = getById(bookingId);
        if (booking != null && booking.getStatus() == 0) {
            booking.setStatus(1);
            updateById(booking);
            if (booking.getUserId() != null) {
                messageService.sendMessage(booking.getUserId(), "订单已确认", 
                    "您的订单 " + booking.getOrderNo() + " 已确认", 1);
            }
        }
    }
    
    @Override
    @Transactional
    public void checkIn(Long bookingId) {
        Booking booking = getById(bookingId);
        if (booking != null && booking.getStatus() == 1) {
            booking.setStatus(2);
            updateById(booking);
            roomService.updateRoomStatus(booking.getRoomId(), 2);
        }
    }
    
    @Override
    @Transactional
    public void checkOut(Long bookingId) {
        Booking booking = getById(bookingId);
        if (booking != null && booking.getStatus() == 2) {
            booking.setStatus(3);
            updateById(booking);
            roomService.updateRoomStatus(booking.getRoomId(), 3);
        }
    }
    
    @Override
    public IPage<Booking> getUserBookings(Long userId, int page, int size) {
        return page(new Page<>(page, size), 
            new LambdaQueryWrapper<Booking>()
                .eq(Booking::getUserId, userId)
                .orderByDesc(Booking::getCreateTime));
    }
    
    @Override
    public IPage<Booking> getAllBookings(int page, int size, Integer status) {
        LambdaQueryWrapper<Booking> wrapper = new LambdaQueryWrapper<Booking>()
            .orderByDesc(Booking::getCreateTime);
        if (status != null) {
            wrapper.eq(Booking::getStatus, status);
        }
        return page(new Page<>(page, size), wrapper);
    }
}
