package com.broadridge.brx.matmapparser.finder.repositoryscan.search;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum GitlabSearchType {
    MATMAP("matmap"),
    YAML("yaml"),
    YML("yml");

    private final String extension;
}
