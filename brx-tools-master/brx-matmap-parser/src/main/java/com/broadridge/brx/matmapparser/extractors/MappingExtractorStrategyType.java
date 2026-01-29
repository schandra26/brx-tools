package com.broadridge.brx.matmapparser.extractors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum MappingExtractorStrategyType {
    DIRECT_BRX("directBRx"),
    VALIDATED("validated"),
    DIRECT_OPEN_API("directOpenAPI");

    private final String name;
}
