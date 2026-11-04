package com.example.responselink.controller;

import com.example.responselink.dto.ResponderRequestDTO;
import com.example.responselink.dto.ResponderResponseDTO;
import com.example.responselink.service.ResponderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/responders")
@RequiredArgsConstructor
public class ResponderController {

    private final ResponderService responderService;

    // Create Responder
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponderResponseDTO createResponder(
            @Valid @RequestBody ResponderRequestDTO request) {

        return responderService.createResponder(request);
    }

    // Get All Responders
    @GetMapping
    public List<ResponderResponseDTO> getAllResponders() {

        return responderService.getAllResponders();
    }
}