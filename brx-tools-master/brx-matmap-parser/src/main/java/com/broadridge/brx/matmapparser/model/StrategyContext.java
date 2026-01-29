package com.broadridge.brx.matmapparser.model;

import com.broadridge.brx.matmapparser.extractors.MappingExtractorStrategyType;
import com.broadridge.brx.matmapparser.parsers.spec.SpecFileParsingStrategyType;
import com.broadridge.brx.matmapparser.persistence.PersistenceStrategyType;

public record StrategyContext(SpecFileParsingStrategyType specFileParsingStrategyType,
                              MappingExtractorStrategyType mappingExtractorStrategyType,
                              PersistenceStrategyType persistenceStrategyType) {
}
