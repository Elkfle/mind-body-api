package com.grupo1.mindbody.attendance.exception;

import com.grupo1.mindbody.shared.exception.BusinessRuleException;

public class InvalidQrCodeException extends BusinessRuleException {
    public InvalidQrCodeException() {
        super("El código QR no corresponde a ninguna reserva válida");
    }
}
