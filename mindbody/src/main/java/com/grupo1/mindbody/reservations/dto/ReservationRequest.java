package com.grupo1.mindbody.reservations.dto;

import jakarta.validation.constraints.NotNull;

public record ReservationRequest(@NotNull Long activityId) {}
