package com.broadridge.brx.matmapparser.parsers.spec;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum SpecFileParsingStrategyType {

    MATMAP("matmap"),
    OPEN_API("openAPI");

    private final String name;

}
