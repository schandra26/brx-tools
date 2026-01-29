package com.broadridge.brx.matmapparser.processor;

import com.broadridge.brx.matmapparser.extractors.DirectBRxMappingExtractorStrategy;
import com.broadridge.brx.matmapparser.extractors.MappingExtractorStrategy;
import com.broadridge.brx.matmapparser.extractors.MappingExtractorStrategyType;
import com.broadridge.brx.matmapparser.finder.repositoryscan.model.SpecFileInput;
import com.broadridge.brx.matmapparser.model.mapping.domain.Mapping;
import com.broadridge.brx.matmapparser.model.SpecFile;
import com.broadridge.brx.matmapparser.model.StrategyContext;
import com.broadridge.brx.matmapparser.parsers.spec.MatmapFileParsingStrategy;
import com.broadridge.brx.matmapparser.parsers.spec.SpecFileParsingStrategy;
import com.broadridge.brx.matmapparser.parsers.spec.SpecFileParsingStrategyType;
import com.broadridge.brx.matmapparser.persistence.DatabasePersistenceStrategy;
import com.broadridge.brx.matmapparser.persistence.PersistenceStrategy;
import com.broadridge.brx.matmapparser.persistence.PersistenceStrategyType;
import com.broadridge.brx.matmapparser.processor.file.SpecFileProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class SpecFileProcessorTest {

    private SpecFileProcessor specFileProcessor;
    private MatmapFileParsingStrategy matmapFileParsingStrategyMock;
    private DirectBRxMappingExtractorStrategy directBRxMappingExtractorStrategyMock;
    private DatabasePersistenceStrategy databasePersistenceStrategyMock;

    @BeforeEach
    void setUp() {
        Map<String, SpecFileParsingStrategy> specFileParsingStrategies = new HashMap<>();
        matmapFileParsingStrategyMock = mock(MatmapFileParsingStrategy.class);
        specFileParsingStrategies.put("matmap", matmapFileParsingStrategyMock);

        Map<String, MappingExtractorStrategy> mappingExtractorStrategies = new HashMap<>();
        directBRxMappingExtractorStrategyMock = mock(DirectBRxMappingExtractorStrategy.class);
        mappingExtractorStrategies.put("directBRx", directBRxMappingExtractorStrategyMock);

        Map<String, PersistenceStrategy> persistenceStrategies = new HashMap<>();
        databasePersistenceStrategyMock = mock(DatabasePersistenceStrategy.class);
        persistenceStrategies.put("database", databasePersistenceStrategyMock);

        specFileProcessor = new SpecFileProcessor(specFileParsingStrategies, mappingExtractorStrategies, persistenceStrategies);
    }

    @Test
    void processResources() {
        List<Resource> resources = Collections.emptyList();
        List<SpecFile> specFiles = Collections.emptyList();
        List<Mapping> mappings = Collections.emptyList();

        specFileProcessor.processResources(
                resources,
                new StrategyContext(
                        SpecFileParsingStrategyType.MATMAP,
                        MappingExtractorStrategyType.DIRECT_BRX,
                        PersistenceStrategyType.DATABASE_MATMAP)
        );

        verify(matmapFileParsingStrategyMock).parseResources(resources);
        verify(directBRxMappingExtractorStrategyMock).extractMappings(specFiles);
        verify(databasePersistenceStrategyMock).persist(mappings, PersistenceStrategyType.DATABASE_MATMAP);
    }

    @Test
    void processSpecFileInputs() {
        List<SpecFileInput> specFileInputs = Collections.emptyList();
        List<SpecFile> specFiles = Collections.emptyList();
        List<Mapping> mappings = Collections.emptyList();

        specFileProcessor.processSpecFileInputs(
                specFileInputs,
                new StrategyContext(SpecFileParsingStrategyType.MATMAP,
                        MappingExtractorStrategyType.DIRECT_BRX,
                        PersistenceStrategyType.DATABASE_MATMAP)
        );

        verify(matmapFileParsingStrategyMock).parseSpecFileInputs(specFileInputs);
        verify(directBRxMappingExtractorStrategyMock).extractMappings(specFiles);
        verify(databasePersistenceStrategyMock).persist(mappings, PersistenceStrategyType.DATABASE_MATMAP);
    }
}