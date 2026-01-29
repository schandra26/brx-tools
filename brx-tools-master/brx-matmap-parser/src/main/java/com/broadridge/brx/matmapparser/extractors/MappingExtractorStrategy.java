package com.broadridge.brx.matmapparser.extractors;

import com.broadridge.brx.matmapparser.model.mapping.domain.Mapping;
import com.broadridge.brx.matmapparser.model.SpecFile;

import java.util.List;

public interface MappingExtractorStrategy {

    List<Mapping> extractMappings(final List<SpecFile> specFiles);

}
