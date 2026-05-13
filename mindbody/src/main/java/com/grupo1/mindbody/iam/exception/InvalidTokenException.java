package com.grupo1.mindbody.iam.exception;

import com.grupo1.mindbody.shared.exception.BusinessRuleException;

public class InvalidTokenException extends BusinessRuleException {
    public InvalidTokenException() {
        super("Token inválido o expirado");
    }
}
