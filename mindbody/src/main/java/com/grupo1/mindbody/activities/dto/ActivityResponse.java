package com.grupo1.mindbody.activities.dto;

import com.grupo1.mindbody.activities.model.ActivityCategory;
import com.grupo1.mindbody.activities.model.ActivityStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record ActivityResponse(
    Long id,
    String title,
    String description,
    ActivityCategory category,
    ActivityStatus status,
    String venue,
    String location,
    int maxCapacity,
    int currentEnrollment,
    LocalDate date,
    LocalTime startTime,
    LocalTime endTime,
    Long institutionId,
    Long createdByAdminId,
    LocalDateTime createdAt
) {}
