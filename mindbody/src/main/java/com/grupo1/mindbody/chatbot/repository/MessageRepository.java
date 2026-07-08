package com.grupo1.mindbody.chatbot.repository;

import com.grupo1.mindbody.chatbot.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByConversationIdOrderBySentAtAsc(Long conversationId);
}
