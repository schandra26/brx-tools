package com.broadridge.brx.matmapparser.parsers.spec;

import com.broadridge.brx.matmapparser.finder.repositoryscan.model.SpecFileInput;
import com.broadridge.brx.matmapparser.model.SpecFile;
import org.springframework.core.io.Resource;

import java.util.List;

public interface SpecFileParsingStrategy {

    /**
     * Parses a list of files received via direct API call
     *
     * @see com.broadridge.brx.matmapparser.api.ParserAPI
     *
     * @param resources list of files to parse
     * @return list of parsed files
     */
    List<SpecFile> parseResources(final List<Resource> resources);


    /**
     * Parses a list of files received via Repository Scan API
     *
     * @see com.broadridge.brx.matmapparser.api.RepositoryScanAPI
     *
     * @param specFileInputs list of files to parse
     * @return list of parsed files
     */
    List<SpecFile> parseSpecFileInputs(final List<SpecFileInput> specFileInputs);

}
