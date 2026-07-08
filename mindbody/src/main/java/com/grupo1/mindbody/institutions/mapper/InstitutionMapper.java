package com.grupo1.mindbody.institutions.mapper;

import com.grupo1.mindbody.institutions.dto.InstitutionRequest;
import com.grupo1.mindbody.institutions.dto.InstitutionResponse;
import com.grupo1.mindbody.institutions.model.Institution;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InstitutionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Institution toEntity(InstitutionRequest request);

    InstitutionResponse toResponse(Institution institution);
}
