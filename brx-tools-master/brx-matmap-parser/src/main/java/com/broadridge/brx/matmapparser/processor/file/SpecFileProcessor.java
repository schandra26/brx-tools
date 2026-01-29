package com.broadridge.brx.matmapparser.processor.file;

import com.broadridge.brx.matmapparser.extractors.MappingExtractorStrategy;
import com.broadridge.brx.matmapparser.extractors.MappingExtractorStrategyType;
import com.broadridge.brx.matmapparser.finder.repositoryscan.model.SpecFileInput;
import com.broadridge.brx.matmapparser.model.mapping.domain.Mapping;
import com.broadridge.brx.matmapparser.model.SpecFile;
import com.broadridge.brx.matmapparser.model.StrategyContext;
import com.broadridge.brx.matmapparser.parsers.spec.SpecFileParsingStrategy;
import com.broadridge.brx.matmapparser.parsers.spec.SpecFileParsingStrategyType;
import com.broadridge.brx.matmapparser.persistence.PersistenceStrategy;
import com.broadridge.brx.matmapparser.persistence.PersistenceStrategyType;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SpecFileProcessor implements FileProcessor {

    private final Map<String, SpecFileParsingStrategy> specFileParsingStrategies;
    private final Map<String, MappingExtractorStrategy> mappingExtractorStrategies;
    private final Map<String, PersistenceStrategy> persistenceStrategies;

    @Override
    public void processResources(List<Resource> resources, StrategyContext strategyContext) {
        List<SpecFile> specFiles = parseSpecFileResources(resources, strategyContext.specFileParsingStrategyType());
        List<Mapping> mappings = extractMappings(specFiles, strategyContext.mappingExtractorStrategyType());
        persistMappings(mappings, strategyContext.persistenceStrategyType());
    }

    @Override
    public void processSpecFileInputs(List<SpecFileInput> specFileInputs, StrategyContext strategyContext) {
        List<SpecFile> specFiles = parseSpecFilesInputs(specFileInputs, strategyContext.specFileParsingStrategyType());
        List<Mapping> mappings = extractMappings(specFiles, strategyContext.mappingExtractorStrategyType());
        persistMappings(mappings, strategyContext.persistenceStrategyType());
    }

    private List<SpecFile> parseSpecFileResources(List<Resource> resources, SpecFileParsingStrategyType specFileParsingStrategyType) {
        SpecFileParsingStrategy specFileParsingStrategy = specFileParsingStrategies.get(specFileParsingStrategyType.getName());
        return specFileParsingStrategy.parseResources(resources);
    }

    private List<SpecFile> parseSpecFilesInputs(List<SpecFileInput> specFileInputs, SpecFileParsingStrategyType specFileParsingStrategyType) {
        SpecFileParsingStrategy specFileParsingStrategy = specFileParsingStrategies.get(specFileParsingStrategyType.getName());
        return specFileParsingStrategy.parseSpecFileInputs(specFileInputs);
    }

    private List<Mapping> extractMappings(List<SpecFile> specFiles, MappingExtractorStrategyType mappingExtractorStrategyType) {
        MappingExtractorStrategy mappingExtractorStrategy = mappingExtractorStrategies.get(mappingExtractorStrategyType.getName());
        return mappingExtractorStrategy.extractMappings(specFiles);
    }

    private void persistMappings(List<Mapping> mappings, PersistenceStrategyType persistenceStrategyType) {
        PersistenceStrategy persistenceStrategy = persistenceStrategies.get(persistenceStrategyType.getName());
        persistenceStrategy.persist(mappings, persistenceStrategyType);
    }
}
