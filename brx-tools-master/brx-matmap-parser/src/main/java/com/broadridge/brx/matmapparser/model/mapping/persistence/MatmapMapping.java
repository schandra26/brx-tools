package com.broadridge.brx.matmapparser.model.mapping.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.time.Instant;

@NoArgsConstructor
@Entity
@Table(name = "matmap_mappings")
public class MatmapMapping extends Mapping {

    @Builder
    public MatmapMapping(Long id, String sourceField, String targetField, String nonBrxEnumerationMetadata, String filename, String lastCommitter, Instant lastCommittedAt, Instant createdAt) {
        super(id, sourceField, targetField, nonBrxEnumerationMetadata, filename, lastCommitter, lastCommittedAt, createdAt);
    }
}
