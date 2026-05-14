package com.grupo1.mindbody.reservations.dto;

import com.grupo1.mindbody.reservations.model.Reservation;
import com.grupo1.mindbody.reservations.model.ReservationStatus;

public record ReservationQrResult(
    Long id,
    Long activityId,
    Long userId,
    ReservationStatus status
) {
    public static ReservationQrResult from(Reservation r) {
        return new ReservationQrResult(r.getId(), r.getActivityId(), r.getUserId(), r.getStatus());
    }
}
