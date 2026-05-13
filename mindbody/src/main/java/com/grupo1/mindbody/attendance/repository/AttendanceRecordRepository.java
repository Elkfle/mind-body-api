package com.grupo1.mindbody.attendance.repository;

import com.grupo1.mindbody.attendance.model.AttendanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long> {

    Optional<AttendanceRecord> findByReservationId(Long reservationId);

    boolean existsByReservationId(Long reservationId);

    List<AttendanceRecord> findByActivityId(Long activityId);

    @Query("SELECT COUNT(a) FROM AttendanceRecord a WHERE a.activityId = :activityId AND a.status = 'CONFIRMED'")
    long countConfirmedByActivityId(Long activityId);
}