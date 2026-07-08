package com.grupo1.mindbody.chatbot.dto;

import com.grupo1.mindbody.chatbot.model.FitnessLevel;

public record UserPreferenceRequest(
    String preferredSports,
    String preferredTimes,
    FitnessLevel fitnessLevel,
    String goals,
    String healthNotes
) {}
