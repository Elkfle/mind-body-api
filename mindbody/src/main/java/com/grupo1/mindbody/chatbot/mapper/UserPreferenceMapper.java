package com.grupo1.mindbody.chatbot.mapper;

import com.grupo1.mindbody.chatbot.dto.UserPreferenceRequest;
import com.grupo1.mindbody.chatbot.dto.UserPreferenceResponse;
import com.grupo1.mindbody.chatbot.model.UserPreference;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserPreferenceMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "completedAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    UserPreference toEntity(UserPreferenceRequest request);

    @Mapping(target = "fitnessLevel", expression = "java(preference.getFitnessLevel() != null ? preference.getFitnessLevel().name() : null)")
    UserPreferenceResponse toResponse(UserPreference preference);
}
