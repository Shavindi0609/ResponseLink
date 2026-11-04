package com.example.responselink.repository;

import com.example.responselink.entity.Incident;
import com.example.responselink.enums.IncidentStatus;
import com.example.responselink.enums.Severity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface IncidentRepository
        extends JpaRepository<Incident, Long> {

    @Query("""
        SELECT DISTINCT i
        FROM Incident i
        LEFT JOIN FETCH i.assignments a
        LEFT JOIN FETCH a.responder
        WHERE i.id = :id
    """)
    Optional<Incident> findDetailsById(@Param("id") Long id);

    @Query("""
        SELECT i FROM Incident i
        WHERE (:status IS NULL OR i.status = :status)
        AND (:severity IS NULL OR i.severity = :severity)
        AND (:startDate IS NULL OR i.createdAt >= :startDate)
        AND (:endDate IS NULL OR i.createdAt <= :endDate)
    """)
    Page<Incident> searchIncidents(
            @Param("status") IncidentStatus status,
            @Param("severity") Severity severity,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );
}