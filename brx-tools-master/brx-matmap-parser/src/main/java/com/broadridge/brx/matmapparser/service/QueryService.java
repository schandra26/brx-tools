package com.broadridge.brx.matmapparser.service;

import com.broadridge.brx.matmapparser.model.mapping.domain.Mapping;
import org.springframework.core.io.Resource;

import java.util.List;

public interface QueryService {

    List<Mapping> getFilteredMappings(String filename, String source, String target);

    Resource getMappingsExcel();

}
