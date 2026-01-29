package com.broadridge.brx.matmapparser.model.mapping.persistence;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@ToString
@MappedSuperclass
public abstract class Mapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sourceField;

    @Column(length = 5000)
    private String targetField;
    @Column(length = 5000)
    private String nonBrxEnumerationMetadata;
    private String filename;
    private String lastCommitter;
    private Instant lastCommittedAt;
    private Instant createdAt;

}
