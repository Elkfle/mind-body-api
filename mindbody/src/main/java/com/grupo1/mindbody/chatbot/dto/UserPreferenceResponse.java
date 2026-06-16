package com.grupo1.mindbody.chatbot.dto;

import java.time.LocalDateTime;

public record UserPreferenceResponse(
    Long id,
    String preferredSports,
    String preferredTimes,
    String fitnessLevel,
    String goals,
    String healthNotes,
    LocalDateTime completedAt
) {}
