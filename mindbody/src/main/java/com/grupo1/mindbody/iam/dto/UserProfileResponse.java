package com.grupo1.mindbody.iam.dto;

import com.grupo1.mindbody.iam.model.User;

public record UserProfileResponse(
    Long id,
    String email,
    String firstName,
    String lastName,
    String phone,
    String institutionName,
    String role,
    boolean active
) {
    public static UserProfileResponse from(User user) {
        return new UserProfileResponse(
            user.getId(),
            user.getEmail(),
            user.getFirstName(),
            user.getLastName(),
            user.getPhone(),
            user.getInstitution() != null ? user.getInstitution().getName() : null,
            user.getRole().name(),
            user.isActive()
        );
    }
}
