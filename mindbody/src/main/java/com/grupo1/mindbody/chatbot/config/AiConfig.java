package com.grupo1.mindbody.chatbot.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * Configuración de Spring AI:
 * <ul>
 *   <li>{@link ChatClient} sobre el modelo de chat autoconfigurado (OpenAI-compatible → Gemini).</li>
 *   <li>{@link VectorStore} en memoria para el RAG, alimentado por el {@link EmbeddingModel}
 *       autoconfigurado por el SDK nativo de Google GenAI (ver {@code application.yml}:
 *       {@code spring.ai.model.embedding.text=google-genai}). Antes se usaba un modelo ONNX
 *       local ({@code TransformersEmbeddingModel}), pero cargarlo en memoria dentro del
 *       proceso Java agotaba el heap en el free tier de Render (512MB).</li>
 * </ul>
 *
 * <p>El {@link VectorStore} se inyecta {@code @Lazy}: si consultar la API de embeddings
 * falla (sin key, sin cuota, sin red), el fallo ocurre recién en el primer uso real y el
 * asistente de nutrición degrada a un 503 controlado, en vez de tumbar todo el backend.
 */
@Configuration
public class AiConfig {

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder.build();
    }

    @Bean
    @Lazy
    public VectorStore vectorStore(EmbeddingModel embeddingModel) {
        return SimpleVectorStore.builder(embeddingModel).build();
    }
}
