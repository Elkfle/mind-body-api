package com.grupo1.mindbody.activities.service;

import com.grupo1.mindbody.activities.dto.ActivityRequest;
import com.grupo1.mindbody.activities.dto.ActivityResponse;
import com.grupo1.mindbody.activities.dto.ActivitySummaryReport;
import com.grupo1.mindbody.activities.model.ActivityCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface IActivityService {
    ActivityResponse create(ActivityRequest request, Long createdByAdminId);
    Page<ActivityResponse> findAll(Pageable pageable);
    Page<ActivityResponse> findByFilters(ActivityCategory category, LocalDate date, String location, Pageable pageable);
    ActivityResponse findById(Long id);
    ActivityResponse update(Long id, ActivityRequest request);
    void cancel(Long id);
    boolean hasAvailableSlots(Long activityId);
    void incrementEnrollment(Long activityId);
    void decrementEnrollment(Long activityId);
    List<ActivitySummaryReport> reportByCategory();
}
