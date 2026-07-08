package com.grupo1.mindbody.chatbot.service;

import com.grupo1.mindbody.chatbot.dto.UserPreferenceRequest;
import com.grupo1.mindbody.chatbot.dto.UserPreferenceResponse;

public interface IUserPreferenceService {
    UserPreferenceResponse save(Long userId, UserPreferenceRequest request);
    UserPreferenceResponse findByUser(Long userId);
    String buildLlmContext(Long userId);
}
