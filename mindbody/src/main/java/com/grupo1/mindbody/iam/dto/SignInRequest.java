package com.grupo1.mindbody.iam.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SignInRequest(
    @NotBlank @Email String email,
    @NotBlank String password
) {}
