package com.grupo1.mindbody.iam.dto;

import com.grupo1.mindbody.iam.model.User;

public record TokenResponse(
    String accessToken,
    String refreshToken,
    String tokenType,
    long expiresIn,
    String email,
    String firstName,
    String lastName,
    String role
) {
    public static TokenResponse of(String accessToken, String refreshToken,
                                    long expiresIn, User user) {
        return new TokenResponse(
            accessToken, refreshToken, "Bearer", expiresIn,
            user.getEmail(), user.getFirstName(), user.getLastName(), user.getRole().name()
        );
    }
}
