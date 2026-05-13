package com.grupo1.mindbody.iam.exception;

import com.grupo1.mindbody.shared.exception.BusinessRuleException;

public class DuplicateEmailException extends BusinessRuleException {
    public DuplicateEmailException(String email) {
        super("El email ya está registrado: " + email);
    }
}
