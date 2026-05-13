package com.grupo1.mindbody.reservations.exception;

import com.grupo1.mindbody.shared.exception.BusinessRuleException;

public class DuplicateReservationException extends BusinessRuleException {
    public DuplicateReservationException() {
        super("Ya tienes una reserva activa para esta actividad");
    }
}
