package com.grupo1.mindbody.activities.dto;

public record ActivitySummaryReport(
    String category,
    Long totalActivities,
    Long totalEnrollment
) {}
