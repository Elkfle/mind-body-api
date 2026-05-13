package com.grupo1.mindbody.iam.service;

import com.grupo1.mindbody.iam.dto.*;
import com.grupo1.mindbody.iam.exception.DuplicateEmailException;
import com.grupo1.mindbody.iam.exception.InvalidTokenException;
import com.grupo1.mindbody.iam.model.RefreshToken;
import com.grupo1.mindbody.iam.model.User;
import com.grupo1.mindbody.iam.repository.RefreshTokenRepository;
import com.grupo1.mindbody.iam.repository.UserRepository;
import com.grupo1.mindbody.iam.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.jwt.expiration-ms}")
    private long accessTokenExpirationMs;

    @Value("${app.jwt.refresh-expiration-days}")
    private long refreshExpirationDays;

    @Override
    @Transactional
    public TokenResponse signUp(SignUpRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateEmailException(request.email());
        }
        User user = User.builder()
            .email(request.email())
            .password(passwordEncoder.encode(request.password()))
            .name(request.name())
            .phone(request.phone())
            .institutionId(request.institutionId())
            .role(request.role())
            .build();
        userRepository.save(user);
        return generateTokens(user);
    }

    @Override
    @Transactional
    public TokenResponse signIn(SignInRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        User user = userRepository.findByEmail(request.email())
            .orElseThrow(InvalidTokenException::new);
        refreshTokenRepository.revokeAllByUserId(user.getId());
        return generateTokens(user);
    }

    @Override
    @Transactional
    public TokenResponse refresh(String refreshTokenValue) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
            .orElseThrow(InvalidTokenException::new);
        if (!refreshToken.isValid()) {
            throw new InvalidTokenException();
        }
        refreshToken.setRevokedAt(LocalDateTime.now());
        refreshTokenRepository.save(refreshToken);
        return generateTokens(refreshToken.getUser());
    }

    @Override
    @Transactional
    public void signOut(String refreshTokenValue) {
        refreshTokenRepository.findByToken(refreshTokenValue)
            .ifPresent(rt -> {
                rt.setRevokedAt(LocalDateTime.now());
                refreshTokenRepository.save(rt);
            });
    }

    @Override
    public UserProfileResponse getProfile(User currentUser) {
        return UserProfileResponse.from(currentUser);
    }

    private TokenResponse generateTokens(User user) {
        String accessToken = jwtService.generateAccessToken(user);
        String refreshTokenValue = UUID.randomUUID().toString();
        RefreshToken refreshToken = RefreshToken.builder()
            .token(refreshTokenValue)
            .user(user)
            .expiresAt(LocalDateTime.now().plusDays(refreshExpirationDays))
            .build();
        refreshTokenRepository.save(refreshToken);
        return TokenResponse.of(accessToken, refreshTokenValue, accessTokenExpirationMs, user);
    }
}
