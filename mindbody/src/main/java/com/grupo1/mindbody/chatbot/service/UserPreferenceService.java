package com.grupo1.mindbody.chatbot.service;

import com.grupo1.mindbody.chatbot.dto.UserPreferenceRequest;
import com.grupo1.mindbody.chatbot.dto.UserPreferenceResponse;
import com.grupo1.mindbody.chatbot.mapper.UserPreferenceMapper;
import com.grupo1.mindbody.chatbot.model.UserPreference;
import com.grupo1.mindbody.chatbot.repository.UserPreferenceRepository;
import com.grupo1.mindbody.iam.model.User;
import com.grupo1.mindbody.iam.repository.UserRepository;
import com.grupo1.mindbody.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserPreferenceService implements IUserPreferenceService {

    private final UserPreferenceRepository preferenceRepository;
    private final UserPreferenceMapper preferenceMapper;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserPreferenceResponse save(Long userId, UserPreferenceRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        UserPreference preference = preferenceRepository.findByUserId(userId)
            .orElseGet(() -> {
                UserPreference p = preferenceMapper.toEntity(request);
                p.setUser(user);
                p.setCompletedAt(LocalDateTime.now());
                return p;
            });

        preference.setPreferredSports(request.preferredSports());
        preference.setPreferredTimes(request.preferredTimes());
        preference.setFitnessLevel(request.fitnessLevel());
        preference.setGoals(request.goals());
        preference.setHealthNotes(request.healthNotes());
        preference.setUpdatedAt(LocalDateTime.now());

        return preferenceMapper.toResponse(preferenceRepository.save(preference));
    }

    @Override
    @Transactional(readOnly = true)
    public UserPreferenceResponse findByUser(Long userId) {
        UserPreference preference = preferenceRepository.findByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Preferencias no encontradas para el usuario"));
        return preferenceMapper.toResponse(preference);
    }

    @Override
    @Transactional(readOnly = true)
    public String buildLlmContext(Long userId) {
        return preferenceRepository.findByUserId(userId)
            .map(p -> {
                StringBuilder sb = new StringBuilder("Preferencias del usuario:\n");
                if (p.getPreferredSports() != null)
                    sb.append("- Deportes preferidos: ").append(p.getPreferredSports()).append("\n");
                if (p.getPreferredTimes() != null)
                    sb.append("- Horarios disponibles: ").append(p.getPreferredTimes()).append("\n");
                if (p.getFitnessLevel() != null)
                    sb.append("- Nivel de fitness: ").append(p.getFitnessLevel().name()).append("\n");
                if (p.getGoals() != null)
                    sb.append("- Objetivos: ").append(p.getGoals()).append("\n");
                return sb.toString();
            })
            .orElse("");
    }
}
