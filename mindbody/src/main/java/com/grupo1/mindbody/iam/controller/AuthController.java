package com.grupo1.mindbody.iam.controller;

import com.grupo1.mindbody.iam.dto.*;
import com.grupo1.mindbody.iam.model.User;
import com.grupo1.mindbody.iam.service.IAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "IAM", description = "Autenticación y gestión de identidad")
public class AuthController {

    private final IAuthService authService;

    @Operation(summary = "Registrar nuevo usuario (estudiante o admin)")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Usuario registrado, tokens generados"),
        @ApiResponse(responseCode = "400", description = "Email duplicado o datos inválidos")
    })
    @PostMapping("/sign-up")
    public ResponseEntity<TokenResponse> signUp(@Valid @RequestBody SignUpRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.signUp(request));
    }

    @Operation(summary = "Iniciar sesión")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Tokens generados"),
        @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    })
    @PostMapping("/sign-in")
    public ResponseEntity<TokenResponse> signIn(@Valid @RequestBody SignInRequest request) {
        return ResponseEntity.ok(authService.signIn(request));
    }

    @Operation(summary = "Renovar access token usando refresh token")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Nuevos tokens generados"),
        @ApiResponse(responseCode = "400", description = "Refresh token inválido o expirado")
    })
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        return ResponseEntity.ok(authService.refresh(request.refreshToken()));
    }

    @Operation(summary = "Cerrar sesión (revoca el refresh token)")
    @ApiResponse(responseCode = "204", description = "Sesión cerrada")
    @PostMapping("/sign-out")
    public ResponseEntity<Void> signOut(@Valid @RequestBody RefreshRequest request) {
        authService.signOut(request.refreshToken());
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Obtener perfil del usuario autenticado",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Perfil del usuario"),
        @ApiResponse(responseCode = "401", description = "Token inválido o ausente")
    })
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> me(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(authService.getProfile(currentUser));
    }
}
