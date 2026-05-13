package com.grupo1.mindbody.reservations.dto;

import com.grupo1.mindbody.reservations.model.Reservation;

import java.time.LocalDateTime;

public record ReservationResponse(
    Long id,
    Long activityId,
    Long userId,
    String status,
    String qrCode,
    LocalDateTime createdAt
) {
    public static ReservationResponse from(Reservation r) {
        return new ReservationResponse(
            r.getId(), r.getActivityId(), r.getUserId(),
            r.getStatus().name(), r.getQrCode(), r.getCreatedAt()
        );
    }
}
