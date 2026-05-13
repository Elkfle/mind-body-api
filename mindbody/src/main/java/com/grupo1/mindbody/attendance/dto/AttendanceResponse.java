package com.grupo1.mindbody.attendance.dto;

import com.grupo1.mindbody.attendance.model.AttendanceStatus;
import java.time.LocalDateTime;

public record AttendanceResponse(
        Long id,
        Long reservationId,
        Long studentId,
        Long activityId,
        AttendanceStatus status,
        LocalDateTime scannedAt
) {}