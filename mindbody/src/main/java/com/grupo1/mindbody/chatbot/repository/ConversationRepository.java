package com.grupo1.mindbody.chatbot.repository;

import com.grupo1.mindbody.chatbot.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    Optional<Conversation> findTopByUserIdAndStatusOrderByStartedAtDesc(Long userId, String status);
}
