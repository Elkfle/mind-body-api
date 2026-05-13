package com.grupo1.mindbody.reservations.service;

import com.grupo1.mindbody.reservations.dto.ReservationResponse;
import com.grupo1.mindbody.reservations.model.Reservation;
import com.grupo1.mindbody.shared.pagination.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IReservationService {
    ReservationResponse create(Long activityId, Long userId);
    PageResponse<ReservationResponse> findByUser(Long userId, Pageable pageable);
    List<ReservationResponse> findByActivity(Long activityId);
    void cancel(Long reservationId, Long userId);
    Optional<Reservation> findByQrCode(String qrCode);
}
