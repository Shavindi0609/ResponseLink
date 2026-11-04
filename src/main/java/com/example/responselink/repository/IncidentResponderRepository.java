package com.example.responselink.repository;

import com.example.responselink.entity.IncidentResponder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IncidentResponderRepository
        extends JpaRepository<IncidentResponder, Long> {

    boolean existsByIncident_IdAndResponder_Id(
            Long incidentId,
            Long responderId
    );

    Optional<IncidentResponder> findByIncident_IdAndResponder_Id(
            Long incidentId,
            Long responderId
    );

}