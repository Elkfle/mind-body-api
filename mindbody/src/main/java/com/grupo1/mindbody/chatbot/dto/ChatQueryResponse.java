package com.grupo1.mindbody.chatbot.dto;

import com.grupo1.mindbody.activities.dto.ActivityResponse;

import java.util.List;

public record ChatQueryResponse(
    Long conversationId,
    String reply,
    String intent,
    List<ActivityResponse> suggestions
) {}
