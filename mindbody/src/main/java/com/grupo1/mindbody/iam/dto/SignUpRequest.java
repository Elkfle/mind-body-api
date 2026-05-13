package com.grupo1.mindbody.iam.dto;

import com.grupo1.mindbody.iam.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SignUpRequest(
    @NotBlank @Email String email,
    @NotBlank @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres") String password,
    @NotBlank String name,
    String phone,
    Long institutionId,
    @NotNull Role role
) {}
