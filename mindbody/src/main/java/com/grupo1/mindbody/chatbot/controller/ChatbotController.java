package com.grupo1.mindbody.chatbot.controller;

import com.grupo1.mindbody.chatbot.dto.ChatQueryRequest;
import com.grupo1.mindbody.chatbot.dto.ChatQueryResponse;
import com.grupo1.mindbody.chatbot.service.IChatbotService;
import com.grupo1.mindbody.iam.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/chatbot")
@RequiredArgsConstructor
@Tag(name = "Chatbot", description = "Asistente conversacional para búsqueda y reserva de actividades (US12, US13, US14)")
@SecurityRequirement(name = "bearerAuth")
public class ChatbotController {

    private final IChatbotService chatbotService;

    @Operation(summary = "Enviar mensaje al chatbot y recibir respuesta con sugerencias")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Respuesta del chatbot generada"),
        @ApiResponse(responseCode = "400", description = "Mensaje vacío"),
        @ApiResponse(responseCode = "503", description = "Servicio de IA no disponible")
    })
    @PostMapping("/query")
    public ResponseEntity<ChatQueryResponse> query(
            @Valid @RequestBody ChatQueryRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(chatbotService.query(request, currentUser.getId()));
    }
}
