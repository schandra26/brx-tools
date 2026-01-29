package com.broadridge.brx.matmapparser.api;

import com.broadridge.brx.matmapparser.model.StrategyContext;
import com.broadridge.brx.matmapparser.processor.file.FileProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

import static com.broadridge.brx.matmapparser.extractors.MappingExtractorStrategyType.*;
import static com.broadridge.brx.matmapparser.parsers.spec.SpecFileParsingStrategyType.*;
import static com.broadridge.brx.matmapparser.persistence.PersistenceStrategyType.DATABASE_MATMAP;
import static com.broadridge.brx.matmapparser.persistence.PersistenceStrategyType.DATABASE_OPENAPI;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ParserAPI {

    private final FileProcessor fileProcessor;

    @PostMapping(value = "/brx-matmap")
    public ResponseEntity<String> parseDirectBrxMatmap(@RequestParam("matmapFiles") MultipartFile... matmapFiles) {
        List<Resource> matmaps = Arrays.stream(matmapFiles)
                .map(MultipartFile::getResource)
                .toList();
        fileProcessor.processResources(matmaps, new StrategyContext(MATMAP, DIRECT_BRX, DATABASE_MATMAP));
        return ResponseEntity.ok().body("Successfully scanned matmap files");
    }

    @PostMapping(value = "/validated-matmap")
    public ResponseEntity<String> parseValidatedMatmap(@RequestParam("matmapFiles") MultipartFile... matmapsFiles) {
        List<Resource> resources = Arrays.stream(matmapsFiles)
                .map(MultipartFile::getResource)
                .toList();
        fileProcessor.processResources(resources, new StrategyContext(MATMAP, VALIDATED, DATABASE_MATMAP));
        return ResponseEntity.ok().body("Successfully scanned validated matmap file");
    }

    @PostMapping(value = "/openapi-spec")
    public ResponseEntity<String> parseOpenAPISpec(@RequestParam("openAPISpecFiles") MultipartFile... openAPISpecFiles) {
        List<Resource> resources = Arrays.stream(openAPISpecFiles)
                .map(MultipartFile::getResource)
                .toList();
        fileProcessor.processResources(resources, new StrategyContext(OPEN_API, DIRECT_OPEN_API, DATABASE_OPENAPI));
        return ResponseEntity.ok().body("Successfully scanned OpenAPI files");
    }
}