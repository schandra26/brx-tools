package com.broadridge.brx.matmapparser.finder.repositoryscan.model;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@Setter
@Getter
@ToString
@EqualsAndHashCode
public class Repository {

    private String projectPath;
    private List<String> filePaths;

}
