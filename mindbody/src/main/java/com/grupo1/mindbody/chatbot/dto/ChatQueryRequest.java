package com.grupo1.mindbody.chatbot.dto;

import jakarta.validation.constraints.NotBlank;

public record ChatQueryRequest(
    @NotBlank(message = "El mensaje no puede estar vacío")
    String message
) {}
