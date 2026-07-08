package com.grupo1.mindbody.institutions.controller;

import com.grupo1.mindbody.institutions.dto.InstitutionRequest;
import com.grupo1.mindbody.institutions.dto.InstitutionResponse;
import com.grupo1.mindbody.institutions.service.IInstitutionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/institutions")
@RequiredArgsConstructor
@Tag(name = "Institutions", description = "Gestión de instituciones universitarias")
@SecurityRequirement(name = "bearerAuth")
public class InstitutionController {

    private final IInstitutionService institutionService;

    @Operation(summary = "Crear una institución")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Institución creada"),
        @ApiResponse(responseCode = "400", description = "Nombre duplicado o datos inválidos")
    })
    @PostMapping
    public ResponseEntity<InstitutionResponse> create(@Valid @RequestBody InstitutionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(institutionService.create(request));
    }

    @Operation(summary = "Listar todas las instituciones")
    @ApiResponse(responseCode = "200", description = "Lista de instituciones")
    @GetMapping
    public ResponseEntity<List<InstitutionResponse>> findAll() {
        return ResponseEntity.ok(institutionService.findAll());
    }

    @Operation(summary = "Obtener institución por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Institución encontrada"),
        @ApiResponse(responseCode = "404", description = "Institución no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<InstitutionResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(institutionService.findById(id));
    }
}
