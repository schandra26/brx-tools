package com.broadridge.brx.matmapparser.persistence;

import com.broadridge.brx.matmapparser.model.mapping.domain.Mapping;
import com.broadridge.brx.matmapparser.persistence.adapter.MappingRepositoryAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("database")
@RequiredArgsConstructor
public class DatabasePersistenceStrategy implements PersistenceStrategy {

    private final Map<String, MappingRepositoryAdapter> mappingRepositories;

    @Override
    public void persist(List<Mapping> brxMapping, PersistenceStrategyType persistenceStrategyType) {
        final MappingRepositoryAdapter mappingRepository = mappingRepositories.get(persistenceStrategyType.getMappingType());

        // We're deleting all the existing mappings for simplicity and
        // because we need logic of how and when to update, which is out of scope
        // for now.
        mappingRepository.deleteAll();
        mappingRepository.saveAll(brxMapping);
    }
}
