package com.grupo1.mindbody.chatbot.controller;

import com.grupo1.mindbody.chatbot.dto.NutritionRequest;
import com.grupo1.mindbody.chatbot.dto.NutritionResponse;
import com.grupo1.mindbody.chatbot.service.NutritionService;
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

/**
 * Asistente de nutrición con IA generativa + RAG (US22). Genera recomendaciones de
 * alimentación alineadas a los objetivos del estudiante, apoyándose en un corpus curado.
 */
@RestController
@RequestMapping("/api/v1/nutrition")
@RequiredArgsConstructor
@Tag(name = "Nutrición IA", description = "Asistente de dietas según objetivos (Spring AI + RAG, US22)")
@SecurityRequirement(name = "bearerAuth")
public class NutritionController {

    private final NutritionService nutritionService;

    @Operation(summary = "Recomendación de alimentación según el objetivo del estudiante (RAG)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Recomendación generada"),
        @ApiResponse(responseCode = "400", description = "Mensaje vacío"),
        @ApiResponse(responseCode = "503", description = "Servicio de IA no disponible")
    })
    @PostMapping("/advice")
    public ResponseEntity<NutritionResponse> advise(
            @Valid @RequestBody NutritionRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(nutritionService.advise(currentUser.getId(), request.message()));
    }
}
