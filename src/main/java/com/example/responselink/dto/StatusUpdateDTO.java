package com.example.responselink.dto;

import com.example.responselink.enums.IncidentStatus;
import jakarta.validation.constraints.NotNull;

public record StatusUpdateDTO(

        @NotNull
        IncidentStatus status

) {
}