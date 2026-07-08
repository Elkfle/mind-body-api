package com.grupo1.mindbody.chatbot.controller;

import com.grupo1.mindbody.chatbot.dto.UserPreferenceRequest;
import com.grupo1.mindbody.chatbot.dto.UserPreferenceResponse;
import com.grupo1.mindbody.chatbot.service.IUserPreferenceService;
import com.grupo1.mindbody.iam.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/preferences")
@RequiredArgsConstructor
@Tag(name = "Preferences", description = "Preferencias deportivas del usuario — contexto base del chatbot")
@SecurityRequirement(name = "bearerAuth")
public class PreferenceController {

    private final IUserPreferenceService preferenceService;

    @Operation(summary = "Guardar o actualizar preferencias del usuario")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Preferencias guardadas"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PostMapping
    public ResponseEntity<UserPreferenceResponse> save(
            @RequestBody UserPreferenceRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(preferenceService.save(currentUser.getId(), request));
    }

    @Operation(summary = "Obtener preferencias del usuario autenticado")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Preferencias encontradas"),
        @ApiResponse(responseCode = "404", description = "Preferencias no configuradas")
    })
    @GetMapping("/me")
    public ResponseEntity<UserPreferenceResponse> findMyPreferences(
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(preferenceService.findByUser(currentUser.getId()));
    }
}
