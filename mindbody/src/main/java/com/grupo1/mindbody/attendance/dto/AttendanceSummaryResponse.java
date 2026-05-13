package com.grupo1.mindbody.attendance.dto;

public record AttendanceSummaryResponse(
        Long activityId,
        long totalReservations,
        long totalAttended,
        double attendanceRate
) {}
