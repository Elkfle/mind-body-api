package com.grupo1.mindbody.institutions.service;

import com.grupo1.mindbody.institutions.dto.InstitutionRequest;
import com.grupo1.mindbody.institutions.dto.InstitutionResponse;

import java.util.List;

public interface IInstitutionService {
    InstitutionResponse create(InstitutionRequest request);
    List<InstitutionResponse> findAll();
    InstitutionResponse findById(Long id);
}
