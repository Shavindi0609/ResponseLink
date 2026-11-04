package com.example.responselink.entity;

import com.example.responselink.enums.IncidentStatus;
import com.example.responselink.enums.Severity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "incidents")
public class Incident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Severity severity;

    @NotBlank
    @Column(name = "incident_type", nullable = false)
    private String incidentType;

    @NotBlank
    @Column(nullable = false)
    private String reporter;

    @NotNull
    @DecimalMin("-90.0")
    @DecimalMax("90.0")
    @Column(nullable = false)
    private Double latitude;

    @NotNull
    @DecimalMin("-180.0")
    @DecimalMax("180.0")
    @Column(nullable = false)
    private Double longitude;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IncidentStatus status = IncidentStatus.REPORTED;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(
            mappedBy = "incident",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<IncidentResponder> assignments = new ArrayList<>();

    @PrePersist
    public void onCreate() {

        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

    }

    @PreUpdate
    public void onUpdate() {

        updatedAt = LocalDateTime.now();

    }
}