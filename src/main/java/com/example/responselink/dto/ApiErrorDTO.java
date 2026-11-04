package com.example.responselink.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record ApiErrorDTO(

        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        Map<String, String> validationErrors

) {
}