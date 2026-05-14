package com.grupo1.mindbody.reservations.service;

import com.grupo1.mindbody.activities.service.IActivityService;
import com.grupo1.mindbody.reservations.dto.ReservationQrResult;
import com.grupo1.mindbody.reservations.dto.ReservationResponse;
import com.grupo1.mindbody.reservations.exception.DuplicateReservationException;
import com.grupo1.mindbody.reservations.exception.ReservationNotFoundException;
import com.grupo1.mindbody.reservations.model.Reservation;
import com.grupo1.mindbody.reservations.model.ReservationStatus;
import com.grupo1.mindbody.reservations.repository.ReservationRepository;
import com.grupo1.mindbody.shared.exception.BusinessRuleException;
import com.grupo1.mindbody.shared.pagination.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservationService implements IReservationService {

    private final ReservationRepository reservationRepository;
    private final IActivityService activityService;

    @Override
    @Transactional
    public ReservationResponse create(Long activityId, Long userId) {
        activityService.findById(activityId);

        if (!activityService.hasAvailableSlots(activityId)) {
            throw new BusinessRuleException("La actividad no tiene cupos disponibles");
        }

        if (reservationRepository.existsByActivityIdAndUserIdAndStatus(
                activityId, userId, ReservationStatus.CONFIRMED)) {
            throw new DuplicateReservationException();
        }

        activityService.incrementEnrollment(activityId);

        Reservation reservation = Reservation.builder()
            .activityId(activityId)
            .userId(userId)
            .status(ReservationStatus.CONFIRMED)
            .qrCode(UUID.randomUUID().toString())
            .build();

        return ReservationResponse.from(reservationRepository.save(reservation));
    }

    @Override
    public PageResponse<ReservationResponse> findByUser(Long userId, Pageable pageable) {
        return PageResponse.from(
            reservationRepository.findByUserId(userId, pageable)
                .map(ReservationResponse::from)
        );
    }

    @Override
    public List<ReservationResponse> findByActivity(Long activityId) {
        activityService.findById(activityId);
        return reservationRepository.findByActivityId(activityId)
            .stream()
            .map(ReservationResponse::from)
            .toList();
    }

    @Override
    @Transactional
    public void cancel(Long reservationId, Long userId) {
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new ReservationNotFoundException(reservationId));

        if (!reservation.getUserId().equals(userId)) {
            throw new BusinessRuleException("No tienes permiso para cancelar esta reserva");
        }

        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new BusinessRuleException("La reserva ya está cancelada");
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);
        activityService.decrementEnrollment(reservation.getActivityId());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ReservationQrResult> findByQrCode(String qrCode) {
        return reservationRepository.findByQrCode(qrCode).map(ReservationQrResult::from);
    }
}
