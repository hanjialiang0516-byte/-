package com.hotel.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hotel.common.Result;
import com.hotel.entity.Booking;
import com.hotel.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {
    
    private final BookingService bookingService;
    
    @PostMapping
    public Result<Booking> createBooking(@RequestBody Booking booking, Authentication auth) {
        booking.setUserId((Long) auth.getPrincipal());
        booking.setSource(0);
        return Result.success(bookingService.createBooking(booking));
    }
    
    @GetMapping
    public Result<IPage<Booking>> getMyBookings(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication auth) {
        return Result.success(bookingService.getUserBookings((Long) auth.getPrincipal(), page, size));
    }
    
    @PostMapping("/{id}/cancel")
    public Result<Void> cancelBooking(@PathVariable Long id) {
        bookingService.cancelBooking(id);
        return Result.success();
    }
}
