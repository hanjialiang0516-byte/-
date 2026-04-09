package com.hotel.service.impl;

import com.hotel.dto.ReportQueryDTO;

import com.hotel.mapper.BookingMapper;
import com.hotel.mapper.RoomMapper;
import com.hotel.mapper.RoomTypeMapper;
import com.hotel.service.AdminReportService;
import com.hotel.vo.ReportExportVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class AdminReportServiceImpl implements AdminReportService {

    private final BookingMapper bookingMapper;
    private final RoomMapper roomMapper;
    private final RoomTypeMapper roomTypeMapper;

    /**
     * 获取经营统计数据（用于导出）
     * @param queryDTO 查询参数（时间范围、房型等）
     * @return Excel 导出数据列表
     */
    @Override
    public List<ReportExportVO> getReportDataForExport(ReportQueryDTO queryDTO) {
        LocalDateTime startDateTime = queryDTO.getStartDate() != null ? LocalDateTime.of(queryDTO.getStartDate(), java.time.LocalTime.MIN) : null; // 设置开始时间为00:00:00
        LocalDateTime endDateTime = queryDTO.getEndDate() != null ?LocalDateTime.of(queryDTO.getEndDate(), java.time.LocalTime.MAX) : null; // 设置结束时间为23:59:59


        return bookingMapper.selectReportExportList(
                startDateTime,
                endDateTime,
                queryDTO.getRoomTypeId(),
                queryDTO.getStatus()
        );
    }
}