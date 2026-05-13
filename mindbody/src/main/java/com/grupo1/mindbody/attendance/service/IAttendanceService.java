package com.grupo1.mindbody.attendance.service;

import com.grupo1.mindbody.attendance.dto.AttendanceResponse;
import com.grupo1.mindbody.attendance.dto.AttendanceSummaryResponse;

import java.util.List;

public interface IAttendanceService {
    AttendanceResponse scan(String qrCode, Long adminId);
    List<AttendanceResponse> findByActivity(Long activityId);
    List<AttendanceResponse> findMyAttendance(Long userId);
    AttendanceSummaryResponse getSummary(Long activityId);
}
