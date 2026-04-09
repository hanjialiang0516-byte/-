package com.hotel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hotel.entity.Booking;
import com.hotel.vo.ReportExportVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface BookingMapper extends BaseMapper<Booking> {
    List<ReportExportVO> selectReportExportList(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("roomTypeId") Long roomTypeId,
            @Param("status") Integer status
    );
}
