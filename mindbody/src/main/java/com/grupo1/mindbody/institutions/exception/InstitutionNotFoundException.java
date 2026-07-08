package com.grupo1.mindbody.institutions.exception;

import com.grupo1.mindbody.shared.exception.ResourceNotFoundException;

public class InstitutionNotFoundException extends ResourceNotFoundException {
    public InstitutionNotFoundException(Long id) {
        super("Institución no encontrada con id: " + id);
    }
}
