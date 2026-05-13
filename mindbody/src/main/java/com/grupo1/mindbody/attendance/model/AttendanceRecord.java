package com.grupo1.mindbody.attendance.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendance_records")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AttendanceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reservation_id", nullable = false, unique = true)
    private Long reservationId;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "activity_id", nullable = false)
    private Long activityId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private AttendanceStatus status = AttendanceStatus.CONFIRMED;

    @Column(name = "scanned_at", nullable = false, updatable = false)
    private LocalDateTime scannedAt;

    @PrePersist
    private void prePersist() {
        scannedAt = LocalDateTime.now();
    }
}