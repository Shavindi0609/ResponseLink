package com.example.responselink.service;

import com.example.responselink.dto.*;
import com.example.responselink.entity.*;
import com.example.responselink.enums.IncidentStatus;
import com.example.responselink.entity.IncidentStatusHistory;
import com.example.responselink.exception.BusinessException;
import com.example.responselink.exception.ResourceNotFoundException;
import com.example.responselink.repository.*;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncidentService {

    private final IncidentRepository incidentRepository;
    private final ResponderRepository responderRepository;
    private final IncidentResponderRepository assignmentRepository;
    private final IncidentStatusHistoryRepository historyRepository;

    // Create Incident
    @Transactional
    public IncidentResponseDTO createIncident(
            IncidentRequestDTO request,
            String actor) {

        Incident incident = new Incident();

        incident.setTitle(request.title());
        incident.setDescription(request.description());
        incident.setSeverity(request.severity());
        incident.setIncidentType(request.incidentType());
        incident.setReporter(request.reporter());
        incident.setLatitude(request.latitude());
        incident.setLongitude(request.longitude());
        incident.setStatus(IncidentStatus.REPORTED);

        Incident savedIncident = incidentRepository.save(incident);

        // Save initial audit history
        IncidentStatusHistory history = new IncidentStatusHistory();

        history.setIncident(savedIncident);
        history.setOldStatus(null);
        history.setNewStatus(IncidentStatus.REPORTED);
        history.setActorReference(actor);

        historyRepository.save(history);

        return mapToResponse(savedIncident);
    }

    // Get All Incidents with Filters and Pagination
    @Transactional(readOnly = true)
    public Page<IncidentResponseDTO> getAllIncidents(
            IncidentStatus status,
            com.example.responselink.enums.Severity severity,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable) {

        if (startDate != null && endDate != null
                && startDate.isAfter(endDate)) {
            throw new BusinessException(
                    "Start date cannot be after end date"
            );
        }

        return incidentRepository.searchIncidents(
                status,
                severity,
                startDate,
                endDate,
                pageable
        ).map(this::mapToResponse);
    }

    // Get Incident By ID
    @Transactional(readOnly = true)
    public IncidentResponseDTO getIncidentById(Long id) {

        Incident incident = incidentRepository.findDetailsById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Incident not found: " + id
                        )
                );

        return mapToResponse(incident);
    }

    // Update Incident Status
    @Transactional
    public IncidentResponseDTO updateStatus(
            Long id,
            IncidentStatus newStatus,
            String actor) {

        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Incident not found: " + id
                        )
                );

        IncidentStatus oldStatus = incident.getStatus();

        if (!isValidTransition(oldStatus, newStatus)) {

            throw new BusinessException(
                    "Invalid status transition: "
                            + oldStatus + " -> " + newStatus
            );
        }

        incident.setStatus(newStatus);

        incidentRepository.save(incident);

        // Save Audit History
        IncidentStatusHistory history = new IncidentStatusHistory();

        history.setIncident(incident);
        history.setOldStatus(oldStatus);
        history.setNewStatus(newStatus);
        history.setActorReference(actor);

        historyRepository.save(history);

        return mapToResponse(incident);
    }

    // Validate Status Workflow
    private boolean isValidTransition(
            IncidentStatus oldStatus,
            IncidentStatus newStatus) {

        return switch (oldStatus) {

            case REPORTED ->
                    newStatus == IncidentStatus.ACKNOWLEDGED;

            case ACKNOWLEDGED ->
                    newStatus == IncidentStatus.RESPONDING;

            case RESPONDING ->
                    newStatus == IncidentStatus.RESOLVED;

            case RESOLVED -> false;

        };
    }

    // Assign Responder
    @Transactional
    public IncidentResponseDTO assignResponder(
            Long incidentId,
            Long responderId) {

        Incident incident = incidentRepository.findById(incidentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Incident not found: " + incidentId
                        )
                );

        if (incident.getStatus() == IncidentStatus.RESOLVED) {
            throw new BusinessException(
                    "Cannot assign responders to a resolved incident"
            );
        }

        Responder responder = responderRepository.findById(responderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Responder not found: " + responderId
                        )
                );

        if (!Boolean.TRUE.equals(responder.getActive())) {
            throw new BusinessException(
                    "Cannot assign an inactive responder"
            );
        }

        if (assignmentRepository.existsByIncident_IdAndResponder_Id(
                incidentId, responderId)) {

            throw new BusinessException(
                    "Responder already assigned to this incident"
            );
        }

        IncidentResponder assignment = new IncidentResponder();

        assignment.setIncident(incident);
        assignment.setResponder(responder);

        assignmentRepository.save(assignment);

        return getIncidentById(incidentId);
    }

    // Remove Responder
    @Transactional
    public void removeResponder(
            Long incidentId,
            Long responderId) {

        Incident incident = incidentRepository.findById(incidentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Incident not found: " + incidentId
                        )
                );

        if (incident.getStatus() == IncidentStatus.RESOLVED) {
            throw new BusinessException(
                    "Cannot remove responders from a resolved incident"
            );
        }

        IncidentResponder assignment =
                assignmentRepository.findByIncident_IdAndResponder_Id(
                        incidentId, responderId
                ).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Assignment not found"
                        )
                );

        assignmentRepository.delete(assignment);
    }

    // Get Audit History
    @Transactional(readOnly = true)
    public List<AuditResponseDTO> getAuditHistory(Long incidentId) {

        if (!incidentRepository.existsById(incidentId)) {
            throw new ResourceNotFoundException(
                    "Incident not found: " + incidentId
            );
        }

        return historyRepository
                .findByIncident_IdOrderByChangedAtAsc(incidentId)
                .stream()
                .map(history -> new AuditResponseDTO(
                        history.getId(),
                        history.getOldStatus(),
                        history.getNewStatus(),
                        history.getActorReference(),
                        history.getChangedAt()
                ))
                .toList();
    }

    // Convert Entity to DTO
    private IncidentResponseDTO mapToResponse(Incident incident) {

        List<ResponderResponseDTO> responders =
                incident.getAssignments()
                        .stream()
                        .map(assignment -> {

                            Responder responder =
                                    assignment.getResponder();

                            return new ResponderResponseDTO(
                                    responder.getId(),
                                    responder.getName(),
                                    responder.getEmail(),
                                    responder.getTeam(),
                                    responder.getActive()
                            );

                        })
                        .toList();

        return new IncidentResponseDTO(
                incident.getId(),
                incident.getTitle(),
                incident.getDescription(),
                incident.getSeverity(),
                incident.getIncidentType(),
                incident.getReporter(),
                incident.getLatitude(),
                incident.getLongitude(),
                incident.getStatus(),
                incident.getCreatedAt(),
                responders
        );
    }
}