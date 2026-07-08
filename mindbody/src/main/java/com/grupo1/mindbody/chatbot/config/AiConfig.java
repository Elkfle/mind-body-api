package com.grupo1.mindbody.chatbot.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.transformers.TransformersEmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de Spring AI:
 * <ul>
 *   <li>{@link ChatClient} sobre el modelo de chat autoconfigurado (OpenAI-compatible → Gemini).</li>
 *   <li>{@link TransformersEmbeddingModel}: embeddings <b>locales</b> (ONNX, all-MiniLM-L6-v2)
 *       para el RAG. Se usan en vez del embedding de Gemini porque su endpoint OpenAI-compat
 *       devuelve respuestas sin el campo {@code index} que Spring AI exige.</li>
 *   <li>{@link VectorStore} en memoria alimentado por esos embeddings (corpus en {@code RagIngestionRunner}).</li>
 * </ul>
 */
@Configuration
public class AiConfig {

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder.build();
    }

    @Bean
    public TransformersEmbeddingModel transformersEmbeddingModel() {
        return new TransformersEmbeddingModel();
    }

    @Bean
    public VectorStore vectorStore(TransformersEmbeddingModel transformersEmbeddingModel) {
        return SimpleVectorStore.builder(transformersEmbeddingModel).build();
    }
}
