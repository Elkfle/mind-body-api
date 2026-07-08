package com.grupo1.mindbody.chatbot.service;

import com.grupo1.mindbody.chatbot.dto.ChatQueryRequest;
import com.grupo1.mindbody.chatbot.dto.ChatQueryResponse;
import com.grupo1.mindbody.chatbot.exception.ChatbotUnavailableException;
import com.grupo1.mindbody.chatbot.model.Conversation;
import com.grupo1.mindbody.chatbot.model.Intent;
import com.grupo1.mindbody.chatbot.model.Message;
import com.grupo1.mindbody.chatbot.model.Sender;
import com.grupo1.mindbody.chatbot.repository.ConversationRepository;
import com.grupo1.mindbody.chatbot.repository.MessageRepository;
import com.grupo1.mindbody.iam.model.User;
import com.grupo1.mindbody.iam.repository.UserRepository;
import com.grupo1.mindbody.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Chatbot conversacional con Spring AI (US12-14). El modelo usa Tool Calling
 * ({@link ChatbotTools}) para buscar y reservar actividades reales; la
 * conversación se persiste (Conversation/Message) para dar contexto de historial.
 */
@Service
@RequiredArgsConstructor
public class ChatbotService implements IChatbotService {

    private static final int HISTORY_LIMIT = 10;

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final IUserPreferenceService preferenceService;
    private final ChatClient chatClient;
    private final ChatbotTools chatbotTools;

    @Override
    @Transactional
    public ChatQueryResponse query(ChatQueryRequest request, Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Conversation conversation = conversationRepository
            .findTopByUserIdAndStatusOrderByStartedAtDesc(userId, "ACTIVE")
            .orElseGet(() -> conversationRepository.save(
                Conversation.builder()
                    .user(user)
                    .status("ACTIVE")
                    .startedAt(LocalDateTime.now())
                    .build()
            ));

        // Historial previo (sin el mensaje actual) como contexto conversacional.
        List<org.springframework.ai.chat.messages.Message> history = recentHistory(conversation.getId());

        String reply;
        try {
            reply = chatClient.prompt()
                .system(buildSystemPrompt(userId))
                .messages(history)
                .user(request.message())
                .toolContext(Map.of("userId", userId))
                .tools(chatbotTools)
                .call()
                .content();
        } catch (ChatbotUnavailableException e) {
            throw e;
        } catch (Exception e) {
            throw new ChatbotUnavailableException("Error al conectar con el servicio de IA: " + e.getMessage());
        }
        if (reply == null || reply.isBlank()) {
            reply = "Lo siento, no pude procesar tu solicitud en este momento.";
        }

        messageRepository.save(Message.builder()
            .conversation(conversation).content(request.message())
            .sender(Sender.USER).sentAt(LocalDateTime.now()).build());
        messageRepository.save(Message.builder()
            .conversation(conversation).content(reply)
            .sender(Sender.BOT).intent(Intent.UNKNOWN).sentAt(LocalDateTime.now()).build());

        return new ChatQueryResponse(conversation.getId(), reply, "ASSISTANT", List.of());
    }

    private List<org.springframework.ai.chat.messages.Message> recentHistory(Long conversationId) {
        List<Message> stored = messageRepository.findByConversationIdOrderBySentAtAsc(conversationId);
        int from = Math.max(0, stored.size() - HISTORY_LIMIT);
        return stored.subList(from, stored.size()).stream()
            .map(m -> m.getSender() == Sender.USER
                ? (org.springframework.ai.chat.messages.Message) new UserMessage(m.getContent())
                : new AssistantMessage(m.getContent()))
            .toList();
    }

    private String buildSystemPrompt(Long userId) {
        StringBuilder sb = new StringBuilder("""
            Eres el asistente virtual de Mind&Body, una plataforma de actividades deportivas
            universitarias en Perú. Ayudas a los estudiantes a encontrar y reservar actividades.

            Usa las herramientas disponibles para: buscar actividades (con o sin filtros),
            buscar por rango de horas libres, reservar, listar las reservas del estudiante y
            cancelar reservas. Cuando muestres actividades, incluye su ID para poder reservarlas.
            Antes de reservar o cancelar, confirma con el estudiante. Responde en español,
            de forma clara y breve. Limítate a temas de actividades deportivas y bienestar;
            si te preguntan otra cosa, indícalo amablemente.
            """);
        String prefs = preferenceService.buildLlmContext(userId);
        if (prefs != null && !prefs.isBlank()) {
            sb.append("\n").append(prefs);
        }
        return sb.toString();
    }
}
