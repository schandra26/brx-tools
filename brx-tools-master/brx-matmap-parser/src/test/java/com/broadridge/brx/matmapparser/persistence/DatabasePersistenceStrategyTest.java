package com.broadridge.brx.matmapparser.persistence;

import com.broadridge.brx.matmapparser.model.mapping.domain.Mapping;
import com.broadridge.brx.matmapparser.persistence.adapter.MappingRepositoryAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.verify;

@ExtendWith({MockitoExtension.class})
class DatabasePersistenceStrategyTest {

    @Mock
    private MappingRepositoryAdapter mappingRepositoryMock;

    private DatabasePersistenceStrategy databasePersistenceStrategy;

    @BeforeEach
    void setUp() {
        Map<String, MappingRepositoryAdapter> mappingRepositories = new HashMap<>();

        mappingRepositories.put("openAPIMapping", mappingRepositoryMock);

        databasePersistenceStrategy = new DatabasePersistenceStrategy(mappingRepositories);
    }

    @Test
    void persist() {
        List<Mapping> mappings = List.of(
                Mapping.builder().filename("test1.yaml").sourceField("sourceField1").targetField("targetField1").build(),
                Mapping.builder().filename("test2.yaml").sourceField("sourceField2").targetField("targetField2").build()
        );

        databasePersistenceStrategy.persist(mappings, PersistenceStrategyType.DATABASE_OPENAPI);

        verify(mappingRepositoryMock).deleteAll();
        verify(mappingRepositoryMock).saveAll(mappings);
    }
}