package com.broadridge.brx.matmapparser.processor.file;

import com.broadridge.brx.matmapparser.finder.repositoryscan.model.SpecFileInput;
import com.broadridge.brx.matmapparser.model.StrategyContext;
import org.springframework.core.io.Resource;

import java.util.List;

public interface FileProcessor {

    void processResources(List<Resource> resource, StrategyContext strategyContext);

    void processSpecFileInputs(List<SpecFileInput> specFileInputs, StrategyContext strategyContext);

}
