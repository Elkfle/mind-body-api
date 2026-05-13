package com.grupo1.mindbody.attendance.dto;

import jakarta.validation.constraints.NotBlank;

public record AttendanceScanRequest(
        @NotBlank(message = "el código QR es obligatorio")
        String qrCode
) {}