package com.example.responselink.service;

import com.example.responselink.dto.ResponderRequestDTO;
import com.example.responselink.dto.ResponderResponseDTO;
import com.example.responselink.entity.Responder;
import com.example.responselink.exception.BusinessException;
import com.example.responselink.repository.ResponderRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResponderService {

    private final ResponderRepository responderRepository;

    // Create Responder
    @Transactional
    public ResponderResponseDTO createResponder(
            ResponderRequestDTO request) {

        if (responderRepository.existsByEmail(request.email())) {
            throw new BusinessException(
                    "Email already registered"
            );
        }

        Responder responder = new Responder();

        responder.setName(request.name());
        responder.setEmail(request.email());
        responder.setTeam(request.team());

        responder.setActive(
                request.active() == null
                        ? true
                        : request.active()
        );

        Responder saved = responderRepository.save(responder);

        return mapToResponse(saved);
    }

    // Get All Responders
    @Transactional(readOnly = true)
    public List<ResponderResponseDTO> getAllResponders() {

        return responderRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Convert Entity to DTO
    private ResponderResponseDTO mapToResponse(
            Responder responder) {

        return new ResponderResponseDTO(
                responder.getId(),
                responder.getName(),
                responder.getEmail(),
                responder.getTeam(),
                responder.getActive()
        );
    }
}