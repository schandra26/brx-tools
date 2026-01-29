package com.broadridge.brx.matmapparser.persistence;

import com.broadridge.brx.matmapparser.model.mapping.domain.Mapping;

import java.util.List;

public interface PersistenceStrategy {

    void persist(List<Mapping> brxMapping, PersistenceStrategyType persistenceStrategyType);

}
