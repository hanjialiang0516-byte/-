package com.hotel.controller.admin;

import com.alibaba.excel.EasyExcel;
import com.hotel.common.Result;
import com.hotel.dto.ReportQueryDTO;
import com.hotel.mapper.BookingMapper;
import com.hotel.mapper.RoomMapper;
import com.hotel.mapper.UserMapper;
import com.hotel.service.AdminReportService;
import com.hotel.vo.ReportExportVO;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/admin/reports")
@RequiredArgsConstructor
@Slf4j
public class AdminReportController {
    
    private final BookingMapper bookingMapper;
    private final RoomMapper roomMapper;
    private final UserMapper userMapper;
    private final AdminReportService AdminReportService;
    
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

    /**
     * 导出经营统计数据为 Excel
     */
    @GetMapping("/export")
    public void exportReport(@ModelAttribute ReportQueryDTO queryDTO, HttpServletResponse response) throws IOException {
        log.info("导出经营统计数据：{}", queryDTO);
        // 1. 设置默认时间范围（最近30天）
        if (queryDTO.getStartDate() == null) {
            queryDTO.setStartDate(LocalDate.now().minusDays(30));
        }
        if (queryDTO.getEndDate() == null) {
            queryDTO.setEndDate(LocalDate.now());
        }

        // 2. 查询数据（传入 DTO）
        List<ReportExportVO> voList = AdminReportService.getReportDataForExport(queryDTO);
        log.info("查询数据：{}条", voList.size());

        // 3. 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");

        // 文件名编码（支持中文）
        String fileName = URLEncoder.encode("经营统计_" + LocalDate.now(), "UTF-8")
                .replaceAll("\\+", "%20");
        response.setHeader("Content-disposition",
                "attachment;filename*=utf-8''" + fileName + ".xlsx");

        // 4. 写入 Excel（使用 VO）
        EasyExcel.write(response.getOutputStream(), ReportExportVO.class)
                .sheet("经营统计")
                .doWrite(voList);
    }
}
