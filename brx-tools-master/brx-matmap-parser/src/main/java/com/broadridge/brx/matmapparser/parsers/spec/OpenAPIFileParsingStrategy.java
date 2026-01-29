package com.broadridge.brx.matmapparser.parsers.spec;

import com.broadridge.brx.matmapparser.finder.repositoryscan.model.SpecFileInput;
import com.broadridge.brx.matmapparser.model.Content;
import com.broadridge.brx.matmapparser.model.SpecFile;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.ParseOptions;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static com.broadridge.brx.matmapparser.util.PathUtil.getFilenameFromFilePath;

@Service("openAPI")
@RequiredArgsConstructor
@Slf4j
public class OpenAPIFileParsingStrategy implements SpecFileParsingStrategy {

    private final OpenAPIV3Parser openAPIV3Parser;

    @Override
    @SneakyThrows
    public List<SpecFile> parseResources(List<Resource> resources) {
        final List<SpecFile> specFiles = new ArrayList<>();
        for (Resource resource : resources) {
            OpenAPI openAPI = parse(resource.getFilename(), resource.getContentAsString(StandardCharsets.UTF_8));
            /*dev-note : SwaggerParseResults.getOpenAPI method that is used to parse the resource returns null if the
            * input content is not an OpenAPI specification*/
            if (openAPI != null) {
                SpecFile specFile = SpecFile.builder()
                        .filename(resource.getFilename())
                        .content(new Content(openAPI))
                        .build();

                specFiles.add(specFile);
            } else {
                log.info("The file {}, is invalid as an OpenAPI spec.", resource.getFilename());
            }
        }
        return specFiles;
    }

    @Override
    @SneakyThrows
    public List<SpecFile> parseSpecFileInputs(List<SpecFileInput> specFileInputs) {
        final List<SpecFile> specFiles = new ArrayList<>();
        final List<SpecFileInput> nonOpenAPISpecFileInputs = new ArrayList<>();

        for (final SpecFileInput specFileInput : specFileInputs) {
            final OpenAPI openAPI = parse(specFileInput.getFilePath(), specFileInput.getContents().getContentAsString(StandardCharsets.UTF_8));
            /*dev-note : SwaggerParseResults.getOpenAPI method that is used to parse the resource returns null if the
             * input content is not an OpenAPI specification*/
            if (openAPI != null) {
                final SpecFile specFile = SpecFile.builder()
                        .filename(getFilenameFromFilePath(specFileInput.getFilePath()))
                        .lastCommitter(specFileInput.getLastCommitter())
                        .lastCommittedAt(specFileInput.getLastCommittedAt())
                        .content(new Content(openAPI))
                        .build();

                specFiles.add(specFile);
            } else {
                log.info("The file {}, is invalid as an OpenAPI spec.", specFileInput.getFilePath());
                nonOpenAPISpecFileInputs.add(specFileInput);
            }
        }
        if (!nonOpenAPISpecFileInputs.isEmpty()) {
            specFileInputs.removeAll(nonOpenAPISpecFileInputs);
        }
        return specFiles;
    }

    private OpenAPI parse(final String filename, final String specFileContent) {
        log.info("Parsing OpenAPI spec file {}", filename);
        final ParseOptions parseOptions = createParseOptions();
        final SwaggerParseResult swaggerParseResult = openAPIV3Parser.readContents(specFileContent, null, parseOptions);
        for (final String errorMessage : swaggerParseResult.getMessages()) {
            log.info("While parsing the file {}, an error was thrown: {}", filename, errorMessage);
        }
        return swaggerParseResult.getOpenAPI();
    }

    private ParseOptions createParseOptions() {
        final ParseOptions parseOptions = new ParseOptions();
        parseOptions.setResolve(true);
        parseOptions.setResolveFully(true);
        return parseOptions;
    }
}
