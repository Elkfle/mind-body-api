package com.grupo1.mindbody.chatbot.service;

import com.grupo1.mindbody.chatbot.dto.ChatQueryRequest;
import com.grupo1.mindbody.chatbot.dto.ChatQueryResponse;

public interface IChatbotService {
    ChatQueryResponse query(ChatQueryRequest request, Long userId);
}
