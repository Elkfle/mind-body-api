package com.grupo1.mindbody.reservations.controller;

import com.grupo1.mindbody.iam.model.User;
import com.grupo1.mindbody.reservations.dto.ReservationRequest;
import com.grupo1.mindbody.reservations.dto.ReservationResponse;
import com.grupo1.mindbody.reservations.service.IReservationService;
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

import java.util.List;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
@Tag(name = "Reservations", description = "Gestión de reservas de actividades deportivas")
@SecurityRequirement(name = "bearerAuth")
public class ReservationController {

    private final IReservationService reservationService;

    @Operation(summary = "Crear reserva para una actividad")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Reserva creada con QR"),
        @ApiResponse(responseCode = "400", description = "Sin cupo o reserva duplicada"),
        @ApiResponse(responseCode = "404", description = "Actividad no encontrada")
    })
    @PostMapping
    public ResponseEntity<ReservationResponse> create(
            @Valid @RequestBody ReservationRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(reservationService.create(request.activityId(), currentUser.getId()));
    }

    @Operation(summary = "Listar mis reservas (paginado)")
    @ApiResponse(responseCode = "200", description = "Lista paginada de reservas")
    @GetMapping
    public ResponseEntity<PageResponse<ReservationResponse>> findMyReservations(
            @AuthenticationPrincipal User currentUser,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(reservationService.findByUser(currentUser.getId(), pageable));
    }

    @Operation(summary = "Listar todas las reservas de una actividad (solo ADMIN)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de reservas"),
        @ApiResponse(responseCode = "404", description = "Actividad no encontrada")
    })
    @GetMapping("/activity/{activityId}")
    public ResponseEntity<List<ReservationResponse>> findByActivity(
            @PathVariable Long activityId) {
        return ResponseEntity.ok(reservationService.findByActivity(activityId));
    }

    @Operation(summary = "Cancelar mi reserva")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Reserva cancelada"),
        @ApiResponse(responseCode = "404", description = "Reserva no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancel(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        reservationService.cancel(id, currentUser.getId());
        return ResponseEntity.noContent().build();
    }
}
