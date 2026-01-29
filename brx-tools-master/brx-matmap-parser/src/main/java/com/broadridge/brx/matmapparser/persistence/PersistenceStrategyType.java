package com.broadridge.brx.matmapparser.persistence;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PersistenceStrategyType {

    DATABASE_MATMAP("database", "matmapMapping"),
    DATABASE_OPENAPI("database", "openAPIMapping");

    private final String name;
    private final String mappingType;
}
