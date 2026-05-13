package com.grupo1.mindbody.attendance.controller;

import com.grupo1.mindbody.attendance.dto.AttendanceResponse;
import com.grupo1.mindbody.attendance.dto.AttendanceSummaryResponse;
import com.grupo1.mindbody.attendance.dto.QrScanRequest;
import com.grupo1.mindbody.attendance.service.IAttendanceService;
import com.grupo1.mindbody.iam.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/attendance")
@RequiredArgsConstructor
@Tag(name = "Attendance", description = "Registro de asistencia mediante código QR")
@SecurityRequirement(name = "bearerAuth")
public class AttendanceController {

    private final IAttendanceService attendanceService;

    @Operation(summary = "Escanear QR y registrar asistencia (solo ADMIN)")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Asistencia registrada"),
        @ApiResponse(responseCode = "400", description = "QR inválido o asistencia duplicada")
    })
    @PostMapping("/scan")
    public ResponseEntity<AttendanceResponse> scan(
            @Valid @RequestBody QrScanRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(attendanceService.scan(request.qrCode(), currentUser.getId()));
    }

    @Operation(summary = "Listar asistentes de una actividad (solo ADMIN)")
    @ApiResponse(responseCode = "200", description = "Lista de registros de asistencia")
    @GetMapping("/activity/{activityId}")
    public ResponseEntity<List<AttendanceResponse>> findByActivity(
            @PathVariable Long activityId) {
        return ResponseEntity.ok(attendanceService.findByActivity(activityId));
    }

    @Operation(summary = "Ver mi historial de asistencia")
    @ApiResponse(responseCode = "200", description = "Mis registros de asistencia")
    @GetMapping("/my")
    public ResponseEntity<List<AttendanceResponse>> findMyAttendance(
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(attendanceService.findMyAttendance(currentUser.getId()));
    }

    @Operation(summary = "Resumen de asistencia de una actividad (solo ADMIN)")
    @ApiResponse(responseCode = "200", description = "Total reservas, asistidos y tasa de asistencia")
    @GetMapping("/activity/{activityId}/summary")
    public ResponseEntity<AttendanceSummaryResponse> getSummary(
            @PathVariable Long activityId) {
        return ResponseEntity.ok(attendanceService.getSummary(activityId));
    }
}
