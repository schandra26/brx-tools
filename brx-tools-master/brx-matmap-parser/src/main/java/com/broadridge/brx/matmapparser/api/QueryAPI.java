package com.broadridge.brx.matmapparser.api;

import com.broadridge.brx.matmapparser.model.mapping.domain.Mapping;
import com.broadridge.brx.matmapparser.service.QueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class QueryAPI {

    private final QueryService queryService;

    @GetMapping(value = "/api/mappings")
    public ResponseEntity<List<Mapping>> getMappingsJson(
            @RequestParam(required = false) String filename,
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String target) {

        List<Mapping> mappings = queryService.getFilteredMappings(filename, source, target);
        return ResponseEntity.ok(mappings);
    }

    @GetMapping(value = "/mappings")
    public String getMappings(
            @RequestParam(required = false) String filename,
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String target,
            Model model) {

        List<Mapping> mappings = queryService.getFilteredMappings(filename, source, target);
        model.addAttribute("mappings", mappings);
        model.addAttribute("filenameFilter", filename);
        model.addAttribute("sourceFilter", source);
        model.addAttribute("targetFilter", target);
        return "mappings";
    }

    @GetMapping(value = "/mappings/excel")
    public ResponseEntity<Resource> getMappingsExcel() {
        Resource excelFile = queryService.getMappingsExcel();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"mappings.xlsx\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelFile);
    }

}
