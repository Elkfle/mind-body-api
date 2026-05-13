package com.grupo1.mindbody.reservations.exception;

import com.grupo1.mindbody.shared.exception.ResourceNotFoundException;

public class ReservationNotFoundException extends ResourceNotFoundException {
    public ReservationNotFoundException(Long id) {
        super("Reserva no encontrada con id: " + id);
    }
}
