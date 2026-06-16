package com.grupo1.mindbody.institutions.dto;

import jakarta.validation.constraints.NotBlank;

public record InstitutionRequest(
    @NotBlank(message = "el nombre es obligatorio")
    String name
) {}
