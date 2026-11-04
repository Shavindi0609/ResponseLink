package com.example.responselink.controller;

import com.example.responselink.dto.*;
import com.example.responselink.enums.IncidentStatus;
import com.example.responselink.enums.Severity;
import com.example.responselink.service.IncidentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/incidents")
@RequiredArgsConstructor
public class IncidentController {

    private final IncidentService incidentService;

    // Create Incident
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public IncidentResponseDTO createIncident(
            @Valid @RequestBody IncidentRequestDTO request,
            Authentication authentication) {

        return incidentService.createIncident(
                request,
                authentication.getName()
        );
    }

    // Get All Incidents
    @GetMapping
    public Page<IncidentResponseDTO> getAllIncidents(

            @RequestParam(required = false)
            IncidentStatus status,

            @RequestParam(required = false)
            Severity severity,

            @RequestParam(required = false)
            LocalDateTime startDate,

            @RequestParam(required = false)
            LocalDateTime endDate,

            @PageableDefault(size = 10)
            Pageable pageable) {

        return incidentService.getAllIncidents(
                status,
                severity,
                startDate,
                endDate,
                pageable
        );
    }

    // Get Incident By ID
    @GetMapping("/{id}")
    public IncidentResponseDTO getIncidentById(
            @PathVariable Long id) {

        return incidentService.getIncidentById(id);
    }

    // Update Status
    @PatchMapping("/{id}/status")
    public IncidentResponseDTO updateStatus(

            @PathVariable Long id,

            @Valid @RequestBody StatusUpdateDTO request,

            Authentication authentication) {

        return incidentService.updateStatus(
                id,
                request.status(),
                authentication.getName()
        );
    }

    // Assign Responder
    @PostMapping("/{id}/responders")
    public IncidentResponseDTO assignResponder(

            @PathVariable Long id,

            @Valid @RequestBody AssignmentRequestDTO request) {

        return incidentService.assignResponder(
                id,
                request.responderId()
        );
    }

    // Remove Responder
    @DeleteMapping("/{id}/responders")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeResponder(

            @PathVariable Long id,

            @RequestParam Long responderId) {

        incidentService.removeResponder(id, responderId);
    }

    // Get Audit History
    @GetMapping("/{id}/audit")
    public List<AuditResponseDTO> getAuditHistory(
            @PathVariable Long id) {

        return incidentService.getAuditHistory(id);
    }
}