package com.grupo1.mindbody.attendance.exception;

import com.grupo1.mindbody.shared.exception.BusinessRuleException;

public class AttendanceAlreadyRegisteredException extends BusinessRuleException {
    public AttendanceAlreadyRegisteredException() {
        super("La asistencia para esta reserva ya fue registrada");
    }
}
