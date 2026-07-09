package com.grupo1.mindbody.chatbot.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.transformers.TransformersEmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * Configuración de Spring AI:
 * <ul>
 *   <li>{@link ChatClient} sobre el modelo de chat autoconfigurado (OpenAI-compatible → Gemini).</li>
 *   <li>{@link EmbeddingModel} local (ONNX, all-MiniLM-L6-v2, vía {@link TransformersEmbeddingModel})
 *       para el RAG. Se usa en vez del embedding de Gemini porque su endpoint OpenAI-compat
 *       devuelve respuestas sin el campo {@code index} que Spring AI exige.</li>
 *   <li>{@link VectorStore} en memoria alimentado por esos embeddings (corpus en {@code RagIngestionRunner}).</li>
 * </ul>
 *
 * <p>Los beans de embeddings/vector store son {@code @Lazy}: el modelo ONNX recién se
 * carga en el primer uso real (indexar o consultar el RAG). Así, si el runtime no
 * soporta sus librerías nativas (p. ej. faltó libstdc++ en la imagen de despliegue),
 * la aplicación arranca igual y solo el asistente de nutrición degrada a un 503
 * controlado, en vez de tumbar todo el backend como bean eager que era antes.
 */
@Configuration
public class AiConfig {

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder.build();
    }

    @Bean
    @Lazy
    public EmbeddingModel embeddingModel() {
        return new TransformersEmbeddingModel();
    }

    @Bean
    @Lazy
    public VectorStore vectorStore(EmbeddingModel embeddingModel) {
        return SimpleVectorStore.builder(embeddingModel).build();
    }
}
