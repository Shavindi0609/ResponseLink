package com.example.responselink.dto;

import com.example.responselink.enums.Severity;
import jakarta.validation.constraints.*;

public record IncidentRequestDTO(

        @NotBlank
        String title,

        String description,

        @NotNull
        Severity severity,

        @NotBlank
        String incidentType,

        @NotBlank
        String reporter,

        @NotNull
        @DecimalMin("-90.0")
        @DecimalMax("90.0")
        Double latitude,

        @NotNull
        @DecimalMin("-180.0")
        @DecimalMax("180.0")
        Double longitude

) {
}