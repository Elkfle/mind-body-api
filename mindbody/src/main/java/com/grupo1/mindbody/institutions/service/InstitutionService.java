package com.grupo1.mindbody.institutions.service;

import com.grupo1.mindbody.institutions.dto.InstitutionRequest;
import com.grupo1.mindbody.institutions.dto.InstitutionResponse;
import com.grupo1.mindbody.institutions.exception.InstitutionNotFoundException;
import com.grupo1.mindbody.institutions.mapper.InstitutionMapper;
import com.grupo1.mindbody.institutions.model.Institution;
import com.grupo1.mindbody.institutions.repository.InstitutionRepository;
import com.grupo1.mindbody.shared.exception.BusinessRuleException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InstitutionService implements IInstitutionService {

    private final InstitutionRepository institutionRepository;
    private final InstitutionMapper institutionMapper;

    @Override
    @Transactional
    public InstitutionResponse create(InstitutionRequest request) {
        if (institutionRepository.existsByName(request.name())) {
            throw new BusinessRuleException("Ya existe una institución con ese nombre");
        }
        Institution institution = institutionMapper.toEntity(request);
        return institutionMapper.toResponse(institutionRepository.save(institution));
    }

    @Override
    @Transactional(readOnly = true)
    public List<InstitutionResponse> findAll() {
        return institutionRepository.findAll().stream()
            .map(institutionMapper::toResponse)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public InstitutionResponse findById(Long id) {
        return institutionRepository.findById(id)
            .map(institutionMapper::toResponse)
            .orElseThrow(() -> new InstitutionNotFoundException(id));
    }
}
