package com.hotel.service;

import com.hotel.dto.ReportQueryDTO;
import com.hotel.vo.ReportExportVO;

import java.util.List;

public interface AdminReportService {
    /**
     * 获取经营统计数据（用于导出）
     * @param queryDTO 查询参数（时间范围、房型等）
     * @return Excel 导出数据列表
     */
    List<ReportExportVO> getReportDataForExport(ReportQueryDTO queryDTO);
}