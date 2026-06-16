package com.grupo1.mindbody.activities.mapper;

import com.grupo1.mindbody.activities.dto.ActivityResponse;
import com.grupo1.mindbody.activities.model.Activity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ActivityMapper {

    @Mapping(target = "institutionId", source = "institution.id")
    @Mapping(target = "institutionName", source = "institution.name")
    ActivityResponse toResponse(Activity activity);
}
