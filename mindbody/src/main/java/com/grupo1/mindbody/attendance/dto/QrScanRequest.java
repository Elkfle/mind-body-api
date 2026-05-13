package com.grupo1.mindbody.attendance.dto;

import jakarta.validation.constraints.NotBlank;

public record QrScanRequest(@NotBlank String qrCode) {}
