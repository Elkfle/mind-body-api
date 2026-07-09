package com.grupo1.mindbody.chatbot.dto;

import java.util.List;

/**
 * Respuesta del asistente de nutrición (US22): la recomendación generada por el LLM
 * a partir del objetivo del estudiante + el contexto recuperado (RAG), y las fuentes
 * del corpus que respaldaron la respuesta.
 */
public record NutritionResponse(
    String reply,
    List<String> sources
) {}
