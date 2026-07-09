package com.grupo1.mindbody.chatbot.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Consulta al asistente de nutrición (US22). El estudiante describe su objetivo o
 * pregunta (p. ej. "quiero bajar grasa, ¿cómo debo comer para entrenar en el gym?").
 */
public record NutritionRequest(
    @NotBlank(message = "El mensaje no puede estar vacío")
    String message
) {}
