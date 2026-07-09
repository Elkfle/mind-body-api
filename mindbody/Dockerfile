FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -q
COPY src ./src
RUN mvn package -DskipTests -q

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
# libstdc++6 es requerido por ONNX Runtime (motor de embeddings locales del RAG de
# nutrición, ver AiConfig/TransformersEmbeddingModel). La imagen alpine (musl libc)
# no es compatible con esos binarios nativos; jammy (Ubuntu, glibc) sí lo es.
RUN apt-get update && apt-get install -y --no-install-recommends libstdc++6 \
    && rm -rf /var/lib/apt/lists/*
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
