package com.grupo1.mindbody.reservations.repository;

import com.grupo1.mindbody.reservations.model.Reservation;
import com.grupo1.mindbody.reservations.model.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    boolean existsByActivityIdAndUserIdAndStatus(Long activityId, Long userId, ReservationStatus status);
    Page<Reservation> findByUserId(Long userId, Pageable pageable);
    List<Reservation> findByActivityId(Long activityId);
    Optional<Reservation> findByQrCode(String qrCode);
}
