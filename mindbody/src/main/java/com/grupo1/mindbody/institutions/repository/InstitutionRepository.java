package com.grupo1.mindbody.institutions.repository;

import com.grupo1.mindbody.institutions.model.Institution;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstitutionRepository extends JpaRepository<Institution, Long> {
    boolean existsByName(String name);
}
