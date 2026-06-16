package com.grupo1.mindbody.institutions.dto;

import java.time.LocalDateTime;

public record InstitutionResponse(
    Long id,
    String name,
    LocalDateTime createdAt
) {}
