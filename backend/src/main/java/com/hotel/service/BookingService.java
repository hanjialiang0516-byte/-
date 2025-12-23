package com.hotel.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hotel.entity.Booking;

public interface BookingService extends IService<Booking> {
    Booking createBooking(Booking booking);
    void cancelBooking(Long bookingId);
    void confirmBooking(Long bookingId);
    void checkIn(Long bookingId);
    void checkOut(Long bookingId);
    IPage<Booking> getUserBookings(Long userId, int page, int size);
    IPage<Booking> getAllBookings(int page, int size, Integer status);
}
