package com.example.responselink.dto;

import jakarta.validation.constraints.NotNull;

public record AssignmentRequestDTO(

        @NotNull
        Long responderId

) {
}