package com.broadridge.brx.matmapparser.parsers.loaders;

import lombok.SneakyThrows;
import org.springframework.core.io.ResourceLoader;

import java.io.InputStream;

public class LocalLogicalModelLoadingStrategy implements LogicalModelLoadingStrategy {

    private final ResourceLoader resourceLoader;
    private final String filePath;

    public LocalLogicalModelLoadingStrategy(ResourceLoader resourceLoader, String filePath) {
        this.resourceLoader = resourceLoader;
        this.filePath = filePath;
    }

    @Override
    @SneakyThrows
    public InputStream loadLogicalModel() {
        return resourceLoader
                .getResource("classpath:" + filePath)
                .getInputStream();
    }
}
