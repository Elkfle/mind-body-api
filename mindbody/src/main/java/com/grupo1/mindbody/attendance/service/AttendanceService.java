package com.grupo1.mindbody.attendance.service;

import com.grupo1.mindbody.attendance.dto.AttendanceResponse;
import com.grupo1.mindbody.attendance.dto.AttendanceSummaryResponse;
import com.grupo1.mindbody.attendance.exception.AttendanceAlreadyRegisteredException;
import com.grupo1.mindbody.attendance.exception.InvalidQrCodeException;
import com.grupo1.mindbody.attendance.model.AttendanceRecord;
import com.grupo1.mindbody.attendance.repository.AttendanceRepository;
import com.grupo1.mindbody.reservations.dto.ReservationQrResult;
import com.grupo1.mindbody.reservations.model.ReservationStatus;
import com.grupo1.mindbody.reservations.service.IReservationService;
import com.grupo1.mindbody.shared.exception.BusinessRuleException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceService implements IAttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final IReservationService reservationService;

    @Override
    @Transactional
    public AttendanceResponse scan(String qrCode, Long adminId) {
        ReservationQrResult reservation = reservationService.findByQrCode(qrCode)
            .orElseThrow(InvalidQrCodeException::new);

        if (reservation.status() != ReservationStatus.CONFIRMED) {
            throw new BusinessRuleException("La reserva asociada al QR no está confirmada");
        }

        if (attendanceRepository.existsByReservationId(reservation.id())) {
            throw new AttendanceAlreadyRegisteredException();
        }

        AttendanceRecord record = AttendanceRecord.builder()
            .reservationId(reservation.id())
            .activityId(reservation.activityId())
            .userId(reservation.userId())
            .scannedByAdminId(adminId)
            .build();

        return AttendanceResponse.from(attendanceRepository.save(record));
    }

    @Override
    public List<AttendanceResponse> findByActivity(Long activityId) {
        return attendanceRepository.findByActivityId(activityId)
            .stream()
            .map(AttendanceResponse::from)
            .toList();
    }

    @Override
    public List<AttendanceResponse> findMyAttendance(Long userId) {
        return attendanceRepository.findByUserId(userId)
            .stream()
            .map(AttendanceResponse::from)
            .toList();
    }

    @Override
    public AttendanceSummaryResponse getSummary(Long activityId) {
        long totalReservations = reservationService.findByActivity(activityId).size();
        long totalAttended = attendanceRepository.countByActivityId(activityId);
        double rate = totalReservations > 0
            ? Math.round((totalAttended * 100.0 / totalReservations) * 10.0) / 10.0
            : 0.0;
        return new AttendanceSummaryResponse(activityId, totalReservations, totalAttended, rate);
    }
}
