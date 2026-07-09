package com.grupo1.mindbody.chatbot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Al arrancar la app, indexa el corpus de nutrición (resources/rag/*.md) en el
 * {@link VectorStore} para el RAG del asistente de dietas (US22).
 *
 * <p>Es resiliente: si falta {@code GEMINI_API_KEY}, se agota la cuota de embeddings, o
 * el modelo local (ONNX) no puede inicializarse en este entorno, registra un aviso y la
 * app arranca igual (el asistente responderá con un 503 controlado en vez de sin RAG).
 */
@Component
public class RagIngestionRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(RagIngestionRunner.class);

    private final VectorStore vectorStore;

    /** {@code @Lazy}: por la misma razón que en {@link NutritionService} — el fallo de
     * inicialización del modelo ONNX debe ocurrir aquí dentro de {@code run()} (ya
     * capturado), no al construir este bean durante el arranque de la aplicación. */
    public RagIngestionRunner(@Lazy VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            Resource[] resources = new PathMatchingResourcePatternResolver()
                .getResources("classpath:/rag/*.md");
            List<Document> docs = new ArrayList<>();
            for (Resource r : resources) {
                String text = r.getContentAsString(StandardCharsets.UTF_8);
                docs.add(Document.builder()
                    .text(text)
                    .metadata(Map.of("source", r.getFilename() == null ? "corpus" : r.getFilename()))
                    .build());
            }
            if (docs.isEmpty()) {
                log.warn("RAG: no se encontró corpus en classpath:/rag/*.md");
                return;
            }
            vectorStore.add(docs);
            log.info("RAG: {} documentos de nutrición indexados en el vector store", docs.size());
        } catch (Exception e) {
            log.warn("RAG: no se pudo indexar el corpus de nutrición "
                + "(¿falta GEMINI_API_KEY o se agotó la cuota de embeddings?): {}", e.getMessage());
        }
    }
}
