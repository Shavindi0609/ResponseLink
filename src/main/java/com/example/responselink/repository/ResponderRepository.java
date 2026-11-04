package com.example.responselink.repository;

import com.example.responselink.entity.Responder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResponderRepository
        extends JpaRepository<Responder, Long> {

    boolean existsByEmail(String email);

}