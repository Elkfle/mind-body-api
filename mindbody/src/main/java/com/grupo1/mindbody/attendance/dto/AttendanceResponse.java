package com.grupo1.mindbody.attendance.dto;

import com.grupo1.mindbody.attendance.model.AttendanceRecord;

import java.time.LocalDateTime;

public record AttendanceResponse(
        Long id,
        Long reservationId,
        Long activityId,
        Long userId,
        Long scannedByAdminId,
        LocalDateTime scannedAt
) {
    public static AttendanceResponse from(AttendanceRecord r) {
        return new AttendanceResponse(
                r.getId(), r.getReservationId(), r.getActivityId(),
                r.getUserId(), r.getScannedByAdminId(), r.getScannedAt()
        );
    }
}
