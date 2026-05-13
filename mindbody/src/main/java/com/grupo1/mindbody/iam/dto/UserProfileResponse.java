package com.grupo1.mindbody.iam.dto;

import com.grupo1.mindbody.iam.model.User;

public record UserProfileResponse(
    Long id,
    String email,
    String name,
    String phone,
    Long institutionId,
    String role,
    boolean active
) {
    public static UserProfileResponse from(User user) {
        return new UserProfileResponse(
            user.getId(), user.getEmail(), user.getName(),
            user.getPhone(), user.getInstitutionId(),
            user.getRole().name(), user.isActive()
        );
    }
}
