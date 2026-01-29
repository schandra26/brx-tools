package com.broadridge.brx.matmapparser.model.mapping.domain;

import lombok.*;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Mapping {
    private Long id;
    private String sourceField;
    private String targetField;
    private String nonBrxEnumerationMetadata;
    private String filename;
    private String lastCommitter;
    private Instant lastCommittedAt;
    private Instant createdAt;

    private String entity;

}
