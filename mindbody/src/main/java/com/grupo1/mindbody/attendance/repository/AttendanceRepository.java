package com.grupo1.mindbody.attendance.repository;

import com.grupo1.mindbody.attendance.model.AttendanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttendanceRepository extends JpaRepository<AttendanceRecord, Long> {
    boolean existsByReservationId(Long reservationId);
    List<AttendanceRecord> findByActivityId(Long activityId);
    List<AttendanceRecord> findByUserId(Long userId);
    long countByActivityId(Long activityId);
}
