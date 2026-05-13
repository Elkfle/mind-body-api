package com.grupo1.mindbody.activities.controller;

import com.grupo1.mindbody.activities.dto.ActivityRequest;
import com.grupo1.mindbody.activities.dto.ActivityResponse;
import com.grupo1.mindbody.activities.service.IActivityService;
import com.grupo1.mindbody.iam.model.User;
import com.grupo1.mindbody.shared.pagination.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/activities")
@RequiredArgsConstructor
@Tag(name = "Activities", description = "Gestión de actividades deportivas")
@SecurityRequirement(name = "bearerAuth")
public class ActivityController {

    private final IActivityService activityService;

    @Operation(summary = "Listar todas las actividades (paginado)")
    @ApiResponse(responseCode = "200", description = "Lista paginada de actividades")
    @GetMapping
    public ResponseEntity<PageResponse<ActivityResponse>> findAll(
            @PageableDefault(size = 10, sort = "date") Pageable pageable) {
        return ResponseEntity.ok(PageResponse.from(activityService.findAll(pageable)));
    }

    @Operation(summary = "Obtener una actividad por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Actividad encontrada"),
        @ApiResponse(responseCode = "404", description = "Actividad no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ActivityResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(activityService.findById(id));
    }

    @Operation(summary = "Crear una actividad (solo ADMIN)")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Actividad creada"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    public ResponseEntity<ActivityResponse> create(
            @Valid @RequestBody ActivityRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(activityService.create(request, currentUser.getId()));
    }

    @Operation(summary = "Actualizar una actividad (solo ADMIN)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Actividad actualizada"),
        @ApiResponse(responseCode = "404", description = "Actividad no encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ActivityResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ActivityRequest request) {
        return ResponseEntity.ok(activityService.update(id, request));
    }

    @Operation(summary = "Cancelar una actividad (solo ADMIN)")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Actividad cancelada"),
        @ApiResponse(responseCode = "404", description = "Actividad no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        activityService.cancel(id);
        return ResponseEntity.noContent().build();
    }
}
