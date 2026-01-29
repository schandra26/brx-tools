package com.broadridge.brx.matmapparser.finder.repositoryscan.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;

import java.net.URL;
import java.util.List;

@AllArgsConstructor
public class RepositoryUrls {

    @NotNull
    @NotEmpty
    public List<URL> urls;

}
