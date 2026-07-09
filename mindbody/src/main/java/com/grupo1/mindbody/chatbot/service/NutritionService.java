package com.grupo1.mindbody.chatbot.service;

import com.grupo1.mindbody.chatbot.dto.NutritionResponse;
import com.grupo1.mindbody.chatbot.exception.ChatbotUnavailableException;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Asistente de nutrición/dietas con RAG (US22). Recupera del {@link VectorStore} el
 * material del corpus más relevante para la consulta del estudiante y lo combina con
 * sus preferencias/objetivos para generar una recomendación con Spring AI.
 */
@Service
public class NutritionService {

    private static final int TOP_K = 3;

    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final IUserPreferenceService preferenceService;

    /**
     * El {@code VectorStore} se inyecta {@code @Lazy}: si su creación real (llamada a la
     * API de embeddings de Google GenAI) falla, el fallo ocurre recién en el primer uso
     * real (dentro de {@link #advise}, que ya lo captura y devuelve 503) y no al arrancar
     * la app.
     */
    public NutritionService(ChatClient chatClient,
                             @Lazy VectorStore vectorStore,
                             IUserPreferenceService preferenceService) {
        this.chatClient = chatClient;
        this.vectorStore = vectorStore;
        this.preferenceService = preferenceService;
    }

    public NutritionResponse advise(Long userId, String question) {
        List<Document> docs;
        try {
            docs = vectorStore.similaritySearch(
                SearchRequest.builder().query(question).topK(TOP_K).build());
        } catch (Exception e) {
            // La búsqueda vectorial usa el modelo de embeddings; si la IA no está
            // disponible (sin key o sin cuota) devolvemos 503 en vez de 500.
            throw new ChatbotUnavailableException("Error al conectar con el servicio de IA: " + e.getMessage());
        }

        String context = docs.stream()
            .map(Document::getText)
            .collect(Collectors.joining("\n\n---\n\n"));

        String prefs = preferenceService.buildLlmContext(userId);

        String system = """
            Eres un orientador de nutrición deportiva de Mind&Body para estudiantes
            universitarios. Responde apoyándote en el CONTEXTO recuperado; no inventes
            datos fuera de él. Da recomendaciones prácticas y alineadas al objetivo del
            estudiante y a las actividades de la plataforma (Yoga, Fútbol, Básquet,
            Natación, Gym, Tenis). Termina SIEMPRE recordando que son pautas educativas,
            no un plan médico, y que ante condiciones de salud consulte a un nutricionista
            colegiado. Responde en español, claro y accionable.

            CONTEXTO:
            %s

            %s
            """.formatted(
                context.isBlank() ? "(no hay material específico indexado)" : context,
                (prefs == null || prefs.isBlank()) ? "" : "PERFIL DEL ESTUDIANTE:\n" + prefs);

        String reply;
        try {
            reply = chatClient.prompt().system(system).user(question).call().content();
        } catch (Exception e) {
            throw new ChatbotUnavailableException("Error al conectar con el servicio de IA: " + e.getMessage());
        }
        if (reply == null || reply.isBlank()) {
            reply = "Lo siento, no pude generar una recomendación en este momento.";
        }

        List<String> sources = docs.stream()
            .map(d -> String.valueOf(d.getMetadata().getOrDefault("source", "corpus")))
            .distinct()
            .toList();

        return new NutritionResponse(reply, sources);
    }
}
