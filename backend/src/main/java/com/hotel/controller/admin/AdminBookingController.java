package com.hotel.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hotel.common.Result;
import com.hotel.entity.Booking;
import com.hotel.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/bookings")
@RequiredArgsConstructor
public class AdminBookingController {
    
    private final BookingService bookingService;
    
    @GetMapping
    public Result<IPage<Booking>> getAllBookings(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer status) {
        return Result.success(bookingService.getAllBookings(page, size, status));
    }
    
    @PostMapping("/{id}/confirm")
    public Result<Void> confirmBooking(@PathVariable Long id) {
        bookingService.confirmBooking(id);
        return Result.success();
    }
    
    @PostMapping("/{id}/check-in")
    public Result<Void> checkIn(@PathVariable Long id) {
        bookingService.checkIn(id);
        return Result.success();
    }
    
    @PostMapping("/{id}/check-out")
    public Result<Void> checkOut(@PathVariable Long id) {
        bookingService.checkOut(id);
        return Result.success();
    }
    
    @PostMapping("/{id}/cancel")
    public Result<Void> cancelBooking(@PathVariable Long id) {
        bookingService.cancelBooking(id);
        return Result.success();
    }
    
    @PostMapping("/phone")
    public Result<Booking> createPhoneBooking(@RequestBody Booking booking) {
        booking.setSource(1);
        return Result.success(bookingService.createBooking(booking));
    }
}
