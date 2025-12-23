package com.hotel.controller.admin;

import com.hotel.common.Result;
import com.hotel.mapper.BookingMapper;
import com.hotel.mapper.RoomMapper;
import com.hotel.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/admin/reports")
@RequiredArgsConstructor
public class AdminReportController {
    
    private final BookingMapper bookingMapper;
    private final RoomMapper roomMapper;
    private final UserMapper userMapper;
    
    @GetMapping("/dashboard")
    public Result<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRooms", roomMapper.selectCount(null));
        stats.put("totalUsers", userMapper.selectCount(null));
        stats.put("totalBookings", bookingMapper.selectCount(null));
        
        // 房间状态统计
        List<Map<String, Object>> roomStats = new ArrayList<>();
        String[] statusNames = {"空闲", "已预订", "入住中", "清洁中", "维修中"};
        for (int i = 0; i < 5; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("status", statusNames[i]);
            final int status = i;
            item.put("count", roomMapper.selectList(null).stream()
                .filter(r -> r.getStatus() == status).count());
            roomStats.add(item);
        }
        stats.put("roomStats", roomStats);
        
        // 订单状态统计
        List<Map<String, Object>> bookingStats = new ArrayList<>();
        String[] bookingStatusNames = {"待确认", "已确认", "已入住", "已退房", "已取消"};
        for (int i = 0; i < 5; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("status", bookingStatusNames[i]);
            final int status = i;
            item.put("count", bookingMapper.selectList(null).stream()
                .filter(b -> b.getStatus() == status).count());
            bookingStats.add(item);
        }
        stats.put("bookingStats", bookingStats);
        
        return Result.success(stats);
    }
}
