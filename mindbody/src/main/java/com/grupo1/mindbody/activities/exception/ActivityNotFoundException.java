package com.grupo1.mindbody.activities.exception;

import com.grupo1.mindbody.shared.exception.ResourceNotFoundException;

public class ActivityNotFoundException extends ResourceNotFoundException {
    public ActivityNotFoundException(Long id) {
        super("Actividad no encontrada con id: " + id);
    }
}
