package com.example.responselink.dto;

import com.example.responselink.enums.IncidentStatus;
import com.example.responselink.enums.Severity;

import java.time.LocalDateTime;
import java.util.List;

public record IncidentResponseDTO(

        Long id,
        String title,
        String description,
        Severity severity,
        String incidentType,
        String reporter,
        Double latitude,
        Double longitude,
        IncidentStatus status,
        LocalDateTime createdAt,
        List<ResponderResponseDTO> responders

) {
}