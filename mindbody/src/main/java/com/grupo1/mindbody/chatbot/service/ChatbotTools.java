package com.grupo1.mindbody.chatbot.service;

import com.grupo1.mindbody.activities.dto.ActivityResponse;
import com.grupo1.mindbody.activities.model.ActivityCategory;
import com.grupo1.mindbody.activities.service.IActivityService;
import com.grupo1.mindbody.reservations.dto.ReservationResponse;
import com.grupo1.mindbody.reservations.service.IReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Herramientas que el LLM puede invocar (Spring AI Tool Calling) para cumplir
 * US12 (buscar/reservar conversacional), US13 (por rango horario) y US14
 * (reservar desde el chat). El {@code userId} del estudiante autenticado viaja
 * por el {@link ToolContext}, no como parámetro visible al modelo.
 */
@Component
@RequiredArgsConstructor
public class ChatbotTools {

    private final IActivityService activityService;
    private final IReservationService reservationService;

    @Tool(description = """
        Lista actividades deportivas disponibles. Todos los filtros son opcionales:
        categoria (una de: YOGA, FOOTBALL, BASKETBALL, SWIMMING, GYM, TENNIS),
        fecha exacta en formato YYYY-MM-DD, y ubicacion (texto parcial del lugar/distrito).
        Devuelve cada actividad con su ID, título, categoría, lugar, fecha, horario y cupos.
        """)
    public String buscarActividades(
            @ToolParam(required = false, description = "Categoría deportiva en MAYÚSCULAS") String categoria,
            @ToolParam(required = false, description = "Fecha exacta YYYY-MM-DD") String fecha,
            @ToolParam(required = false, description = "Parte del nombre del lugar o distrito") String ubicacion) {

        ActivityCategory cat = parseCategory(categoria);
        LocalDate date = parseDate(fecha);
        boolean hasFilter = cat != null || date != null || (ubicacion != null && !ubicacion.isBlank());

        Page<ActivityResponse> page = hasFilter
            ? activityService.findByFilters(cat, date, ubicacion, PageRequest.of(0, 15))
            : activityService.findAll(PageRequest.of(0, 15));

        List<ActivityResponse> activities = page.getContent();
        if (activities.isEmpty()) {
            return "No hay actividades que coincidan con esos criterios.";
        }
        return activities.stream().map(this::format).collect(Collectors.joining("\n"));
    }

    @Tool(description = """
        Busca actividades cuyo horario de inicio caiga dentro de un rango de horas del día
        (por ejemplo entre las 08:00 y las 12:00). Útil cuando el estudiante indica sus
        horas libres. Formato de hora: HH:mm (24h). Devuelve las actividades que encajan.
        """)
    public String buscarPorRangoHorario(
            @ToolParam(description = "Hora mínima de inicio, formato HH:mm") String horaDesde,
            @ToolParam(description = "Hora máxima de inicio, formato HH:mm") String horaHasta) {

        java.time.LocalTime desde = parseTime(horaDesde);
        java.time.LocalTime hasta = parseTime(horaHasta);
        if (desde == null || hasta == null) {
            return "Indica el rango de horas en formato HH:mm (por ejemplo 08:00 y 12:00).";
        }
        List<ActivityResponse> matches = activityService.findAll(PageRequest.of(0, 50)).getContent().stream()
            .filter(a -> a.startTime() != null
                && !a.startTime().isBefore(desde)
                && !a.startTime().isAfter(hasta))
            .limit(15)
            .toList();
        if (matches.isEmpty()) {
            return "No hay actividades que empiecen entre " + horaDesde + " y " + horaHasta + ".";
        }
        return matches.stream().map(this::format).collect(Collectors.joining("\n"));
    }

    @Tool(description = """
        Reserva un cupo para el estudiante actual en una actividad, dado el ID de la actividad.
        Úsala solo cuando el estudiante confirme que quiere reservar. Devuelve el resultado.
        """)
    public String reservarActividad(
            @ToolParam(description = "ID numérico de la actividad a reservar") Long activityId,
            ToolContext toolContext) {
        Long userId = currentUserId(toolContext);
        try {
            ReservationResponse r = reservationService.create(activityId, userId);
            return "Reserva confirmada (ID de reserva: " + r.id() + ", estado: " + r.status()
                + "). El estudiante recibe un código QR para el acceso.";
        } catch (Exception e) {
            return "No se pudo reservar: " + e.getMessage();
        }
    }

    @Tool(description = """
        Lista las reservas del estudiante actual (activas y pasadas), con el ID de reserva,
        la actividad y su estado. Útil para consultar su horario o antes de cancelar.
        """)
    public String misReservas(ToolContext toolContext) {
        Long userId = currentUserId(toolContext);
        List<ReservationResponse> reservas = reservationService
            .findByUser(userId, PageRequest.of(0, 30)).content();
        if (reservas.isEmpty()) {
            return "El estudiante no tiene reservas registradas.";
        }
        return reservas.stream()
            .map(r -> "- Reserva #" + r.id() + " | actividad " + r.activityId() + " | estado " + r.status())
            .collect(Collectors.joining("\n"));
    }

    @Tool(description = """
        Cancela una reserva del estudiante actual, dado el ID de la reserva.
        Úsala solo cuando el estudiante confirme la cancelación.
        """)
    public String cancelarReserva(
            @ToolParam(description = "ID numérico de la reserva a cancelar") Long reservationId,
            ToolContext toolContext) {
        Long userId = currentUserId(toolContext);
        try {
            reservationService.cancel(reservationId, userId);
            return "Reserva #" + reservationId + " cancelada. El cupo quedó liberado.";
        } catch (Exception e) {
            return "No se pudo cancelar: " + e.getMessage();
        }
    }

    // ---- helpers ----

    private Long currentUserId(ToolContext toolContext) {
        Object userId = toolContext.getContext().get("userId");
        if (userId instanceof Long l) return l;
        if (userId instanceof Number n) return n.longValue();
        throw new IllegalStateException("No se encontró el usuario en el contexto de la herramienta");
    }

    private String format(ActivityResponse a) {
        return String.format("- [ID:%d] %s (%s) — %s, %s — %s %s-%s — cupos %d/%d — %s",
            a.id(), a.title(), a.category(), a.venue(), a.location(),
            a.date(), a.startTime(), a.endTime(),
            a.currentEnrollment(), a.maxCapacity(), a.status());
    }

    private ActivityCategory parseCategory(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return ActivityCategory.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return LocalDate.parse(value.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private java.time.LocalTime parseTime(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return java.time.LocalTime.parse(value.trim());
        } catch (Exception e) {
            return null;
        }
    }
}
