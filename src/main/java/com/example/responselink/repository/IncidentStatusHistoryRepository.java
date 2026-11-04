package com.example.responselink.repository;

import com.example.responselink.entity.IncidentStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IncidentStatusHistoryRepository
        extends JpaRepository<IncidentStatusHistory, Long> {

    List<IncidentStatusHistory> findByIncident_IdOrderByChangedAtAsc(
            Long incidentId
    );

}