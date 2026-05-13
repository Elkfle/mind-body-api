package com.grupo1.mindbody.activities.dto;

import com.grupo1.mindbody.activities.model.ActivityCategory;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalTime;

public record ActivityRequest(
    @NotBlank(message = "el título es obligatorio")
    String title,

    String description,

    @NotNull(message = "la categoría es obligatoria")
    ActivityCategory category,

    @NotBlank(message = "el lugar es obligatorio")
    String venue,

    @NotBlank(message = "la ubicación es obligatoria")
    String location,

    @Min(value = 1, message = "la capacidad debe ser mayor a 0")
    int maxCapacity,

    @NotNull(message = "la fecha es obligatoria")
    @Future(message = "la fecha debe ser futura")
    LocalDate date,

    @NotNull(message = "la hora de inicio es obligatoria")
    LocalTime startTime,

    @NotNull(message = "la hora de fin es obligatoria")
    LocalTime endTime,

    @NotNull(message = "el institutionId es obligatorio")
    Long institutionId
) {}
