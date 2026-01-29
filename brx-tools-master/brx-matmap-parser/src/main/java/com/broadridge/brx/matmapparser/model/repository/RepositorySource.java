package com.broadridge.brx.matmapparser.model.repository;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "repository_sources")
public class RepositorySource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String url;

    @Column(nullable = false)
    private String type; // GITLAB, GITHUB, etc.

    @Column(nullable = false)
    private boolean enabled;

    @Column(name = "last_scanned_at")
    private Instant lastScannedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = Instant.now();
        }
    }
}
