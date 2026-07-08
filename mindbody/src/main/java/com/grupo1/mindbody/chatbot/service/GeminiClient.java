package com.grupo1.mindbody.chatbot.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grupo1.mindbody.chatbot.exception.ChatbotUnavailableException;
import com.grupo1.mindbody.chatbot.model.Message;
import com.grupo1.mindbody.chatbot.model.Sender;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GeminiClient {

    private final ObjectMapper objectMapper;

    @Value("${app.gemini.api-key:}")
    private String apiKey;

    @Value("${app.gemini.model:gemini-2.0-flash}")
    private String model;

    public String chat(String systemPrompt, List<Message> history) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new ChatbotUnavailableException(
                "El chatbot no está disponible: configura GEMINI_API_KEY en las variables de entorno");
        }

        // Gemini v1 no soporta systemInstruction: se inyecta como primer turno del historial
        List<Map<String, Object>> contents = new ArrayList<>();
        contents.add(Map.of("role", "user",
            "parts", List.of(Map.of("text", systemPrompt))));
        contents.add(Map.of("role", "model",
            "parts", List.of(Map.of("text", "Entendido. Responderé siempre con JSON válido según las instrucciones."))));

        for (Message msg : history) {
            String role = msg.getSender() == Sender.USER ? "user" : "model";
            contents.add(Map.of(
                "role", role,
                "parts", List.of(Map.of("text", msg.getContent()))
            ));
        }

        Map<String, Object> requestBody = new LinkedHashMap<>();
        requestBody.put("contents", contents);

        try {
            RestClient restClient = RestClient.create();
            String endpoint = "https://generativelanguage.googleapis.com/v1/models/"
                + model + ":generateContent";

            String response = restClient.post()
                .uri(endpoint)
                .header("x-goog-api-key", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .body(String.class);

            JsonNode root = objectMapper.readTree(response);
            return root.path("candidates").get(0)
                .path("content").path("parts").get(0)
                .path("text").asText();
        } catch (ChatbotUnavailableException e) {
            throw e;
        } catch (Exception e) {
            throw new ChatbotUnavailableException("Error al conectar con el servicio de IA: " + e.getMessage());
        }
    }
}
