package com.grupo1.mindbody.activities.mapper;

import com.grupo1.mindbody.activities.dto.ActivityResponse;
import com.grupo1.mindbody.activities.model.Activity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ActivityMapper {
    ActivityResponse toResponse(Activity activity);
}
