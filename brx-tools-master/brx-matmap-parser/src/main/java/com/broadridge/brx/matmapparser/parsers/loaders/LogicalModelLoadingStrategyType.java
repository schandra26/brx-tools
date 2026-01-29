package com.broadridge.brx.matmapparser.parsers.loaders;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum LogicalModelLoadingStrategyType {
    LOCAL("local"),
    GITLAB_REPOSITORY("gitlabRepository");

    private final String name;

    public static LogicalModelLoadingStrategyType getValueByName(final String name) {
        for (final LogicalModelLoadingStrategyType value : LogicalModelLoadingStrategyType.values()) {
            if (value.getName().equals(name)) {
                return value;
            }
        }
        return null;
    }
}
