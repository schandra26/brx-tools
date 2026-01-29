package com.broadridge.brx.matmapparser.persistence.adapter;

import com.broadridge.brx.matmapparser.model.mapping.domain.Mapping;

import java.util.List;

public interface MappingRepositoryAdapter {

    List<Mapping> findAll();

    void saveAll(List<Mapping> mappings);

    void deleteAll();
}
