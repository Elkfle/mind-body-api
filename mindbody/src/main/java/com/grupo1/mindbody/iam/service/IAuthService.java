package com.grupo1.mindbody.iam.service;

import com.grupo1.mindbody.iam.dto.*;
import com.grupo1.mindbody.iam.model.User;

public interface IAuthService {
    TokenResponse signUp(SignUpRequest request);
    TokenResponse signIn(SignInRequest request);
    TokenResponse refresh(String refreshToken);
    void signOut(String refreshToken);
    UserProfileResponse getProfile(User currentUser);
}
