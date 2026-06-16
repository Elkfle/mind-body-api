package com.grupo1.mindbody.activities.service;

import com.grupo1.mindbody.activities.dto.ActivityRequest;
import com.grupo1.mindbody.activities.dto.ActivityResponse;
import com.grupo1.mindbody.activities.dto.ActivitySummaryReport;
import com.grupo1.mindbody.activities.exception.ActivityNotFoundException;
import com.grupo1.mindbody.activities.mapper.ActivityMapper;
import com.grupo1.mindbody.activities.model.Activity;
import com.grupo1.mindbody.activities.model.ActivityStatus;
import com.grupo1.mindbody.activities.repository.ActivityRepository;
import com.grupo1.mindbody.institutions.exception.InstitutionNotFoundException;
import com.grupo1.mindbody.institutions.model.Institution;
import com.grupo1.mindbody.institutions.repository.InstitutionRepository;
import com.grupo1.mindbody.shared.exception.BusinessRuleException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityService implements IActivityService {

    private final ActivityRepository activityRepository;
    private final InstitutionRepository institutionRepository;
    private final ActivityMapper activityMapper;

    @Override
    @Transactional
    public ActivityResponse create(ActivityRequest request, Long adminId) {
        Institution institution = institutionRepository.findById(request.institutionId())
            .orElseThrow(() -> new InstitutionNotFoundException(request.institutionId()));
        Activity activity = Activity.builder()
            .title(request.title())
            .description(request.description())
            .category(request.category())
            .status(ActivityStatus.ACTIVE)
            .venue(request.venue())
            .location(request.location())
            .maxCapacity(request.maxCapacity())
            .date(request.date())
            .startTime(request.startTime())
            .endTime(request.endTime())
            .institution(institution)
            .createdByAdminId(adminId)
            .build();
        return activityMapper.toResponse(activityRepository.save(activity));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ActivityResponse> findAll(Pageable pageable) {
        return activityRepository.findAll(pageable)
            .map(activityMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ActivityResponse findById(Long id) {
        return activityRepository.findById(id)
            .map(activityMapper::toResponse)
            .orElseThrow(() -> new ActivityNotFoundException(id));
    }

    @Override
    @Transactional
    public ActivityResponse update(Long id, ActivityRequest request) {
        Activity activity = activityRepository.findById(id)
            .orElseThrow(() -> new ActivityNotFoundException(id));

        activity.setTitle(request.title());
        activity.setDescription(request.description());
        activity.setCategory(request.category());
        activity.setVenue(request.venue());
        activity.setLocation(request.location());
        activity.setMaxCapacity(request.maxCapacity());
        activity.setDate(request.date());
        activity.setStartTime(request.startTime());
        activity.setEndTime(request.endTime());

        return activityMapper.toResponse(activityRepository.save(activity));
    }

    @Override
    @Transactional
    public void cancel(Long id) {
        Activity activity = activityRepository.findById(id)
            .orElseThrow(() -> new ActivityNotFoundException(id));
        activity.setStatus(ActivityStatus.CANCELLED);
        activityRepository.save(activity);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasAvailableSlots(Long activityId) {
        Activity activity = activityRepository.findById(activityId)
            .orElseThrow(() -> new ActivityNotFoundException(activityId));
        return activity.getCurrentEnrollment() < activity.getMaxCapacity();
    }

    @Override
    @Transactional
    public void incrementEnrollment(Long activityId) {
        Activity activity = activityRepository.findById(activityId)
            .orElseThrow(() -> new ActivityNotFoundException(activityId));
        if (activity.getCurrentEnrollment() >= activity.getMaxCapacity()) {
            throw new BusinessRuleException("La actividad ya no tiene cupo disponible");
        }
        activity.setCurrentEnrollment(activity.getCurrentEnrollment() + 1);
        activityRepository.save(activity);
    }

    @Override
    @Transactional
    public void decrementEnrollment(Long activityId) {
        Activity activity = activityRepository.findById(activityId)
            .orElseThrow(() -> new ActivityNotFoundException(activityId));
        if (activity.getCurrentEnrollment() > 0) {
            activity.setCurrentEnrollment(activity.getCurrentEnrollment() - 1);
            activityRepository.save(activity);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActivitySummaryReport> reportByCategory() {
        return activityRepository.reportByCategoryRaw().stream()
            .map(r -> new ActivitySummaryReport(
                (String) r[0],
                ((Number) r[1]).longValue(),
                r[2] != null ? ((Number) r[2]).longValue() : 0L))
            .toList();
    }
}
