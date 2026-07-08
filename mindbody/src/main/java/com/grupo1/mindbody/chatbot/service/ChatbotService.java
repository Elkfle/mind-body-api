package com.grupo1.mindbody.chatbot.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grupo1.mindbody.activities.dto.ActivityResponse;
import com.grupo1.mindbody.activities.service.IActivityService;
import com.grupo1.mindbody.chatbot.dto.ChatQueryRequest;
import com.grupo1.mindbody.chatbot.dto.ChatQueryResponse;
import com.grupo1.mindbody.chatbot.model.*;
import com.grupo1.mindbody.chatbot.repository.ConversationRepository;
import com.grupo1.mindbody.chatbot.repository.MessageRepository;
import com.grupo1.mindbody.iam.model.User;
import com.grupo1.mindbody.iam.repository.UserRepository;
import com.grupo1.mindbody.reservations.service.IReservationService;
import com.grupo1.mindbody.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatbotService implements IChatbotService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final IActivityService activityService;
    private final IReservationService reservationService;
    private final IUserPreferenceService preferenceService;
    private final GeminiClient geminiClient;
    private final ObjectMapper objectMapper;

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

        Message userMessage = Message.builder()
            .conversation(conversation)
            .content(request.message())
            .sender(Sender.USER)
            .sentAt(LocalDateTime.now())
            .build();
        messageRepository.save(userMessage);

        String systemPrompt = buildSystemPrompt(userId);
        List<Message> history = messageRepository
            .findByConversationIdOrderBySentAtAsc(conversation.getId());
        if (history.size() > 10) {
            history = history.subList(history.size() - 10, history.size());
        }

        String rawResponse = geminiClient.chat(systemPrompt, history);
        AiResponse aiResponse = parseAiResponse(rawResponse);

        List<ActivityResponse> suggestions = new ArrayList<>();
        if (aiResponse.intent() == Intent.MAKE_RESERVATION && aiResponse.activityId() != null) {
            try {
                reservationService.create(aiResponse.activityId(), userId);
            } catch (Exception ignored) {
                // El mensaje de error ya va en el reply del LLM
            }
        } else if (aiResponse.intent() == Intent.SEARCH_ACTIVITY) {
            suggestions = activityService.findAll(PageRequest.of(0, 5)).getContent();
        }

        Message botMessage = Message.builder()
            .conversation(conversation)
            .content(aiResponse.reply())
            .sender(Sender.BOT)
            .intent(aiResponse.intent())
            .sentAt(LocalDateTime.now())
            .build();
        messageRepository.save(botMessage);

        return new ChatQueryResponse(
            conversation.getId(),
            aiResponse.reply(),
            aiResponse.intent().name(),
            suggestions
        );
    }

    private String buildSystemPrompt(Long userId) {
        StringBuilder sb = new StringBuilder("""
            Eres un asistente de Mind&Body, plataforma de actividades deportivas universitarias en Perú.
            Tu objetivo es ayudar a los estudiantes a encontrar y reservar actividades deportivas.

            RESTRICCIÓN IMPORTANTE: Solo puedes responder preguntas relacionadas con actividades deportivas.
            Si el usuario pregunta algo fuera de este scope, responde que solo puedes ayudar con actividades.

            Responde SIEMPRE con un JSON válido (sin markdown), con este formato exacto:
            {"reply": "tu respuesta en español", "intent": "SEARCH_ACTIVITY|MAKE_RESERVATION|CHECK_MY_SCHEDULE|CANCEL_RESERVATION|UNKNOWN", "activityId": null_o_número_entero}

            """);

        String preferenceContext = preferenceService.buildLlmContext(userId);
        if (!preferenceContext.isBlank()) {
            sb.append(preferenceContext).append("\n");
        }

        List<ActivityResponse> activities = activityService.findAll(PageRequest.of(0, 20)).getContent();
        if (!activities.isEmpty()) {
            sb.append("Actividades disponibles actualmente:\n");
            for (ActivityResponse a : activities) {
                sb.append(String.format("- [ID:%d] %s (%s) — %s — %s %s-%s — Cupos: %d/%d\n",
                    a.id(), a.title(), a.category(), a.venue(),
                    a.date(), a.startTime(), a.endTime(),
                    a.currentEnrollment(), a.maxCapacity()));
            }
        }

        return sb.toString();
    }

    private AiResponse parseAiResponse(String rawResponse) {
        try {
            JsonNode node = objectMapper.readTree(rawResponse);
            String reply = node.path("reply").asText("Lo siento, no pude procesar tu solicitud.");
            Intent intent;
            try {
                intent = Intent.valueOf(node.path("intent").asText("UNKNOWN"));
            } catch (IllegalArgumentException e) {
                intent = Intent.UNKNOWN;
            }
            Long activityId = node.path("activityId").isNull() ? null : node.path("activityId").asLong();
            return new AiResponse(reply, intent, activityId);
        } catch (Exception e) {
            return new AiResponse(rawResponse, Intent.UNKNOWN, null);
        }
    }

    private record AiResponse(String reply, Intent intent, Long activityId) {}
}
