package com.example.responselink.dto;

import com.example.responselink.enums.IncidentStatus;

import java.time.LocalDateTime;

public record AuditResponseDTO(

        Long id,
        IncidentStatus oldStatus,
        IncidentStatus newStatus,
        String actorReference,
        LocalDateTime changedAt

) {
}