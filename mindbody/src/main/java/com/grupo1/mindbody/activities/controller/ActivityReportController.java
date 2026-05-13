package com.grupo1.mindbody.activities.controller;

import com.grupo1.mindbody.activities.dto.ActivitySummaryReport;
import com.grupo1.mindbody.activities.service.IActivityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/activities/reports")
@RequiredArgsConstructor
@Tag(name = "Activity Reports", description = "Reportes analíticos de actividades")
public class ActivityReportController {

    private final IActivityService activityService;

    @Operation(summary = "Reporte de actividades agrupadas por categoría")
    @ApiResponse(responseCode = "200", description = "Reporte generado exitosamente")
    @GetMapping("/by-category")
    public ResponseEntity<List<ActivitySummaryReport>> reportByCategory() {
        return ResponseEntity.ok(activityService.reportByCategory());
    }
}
