package com.broadridge.brx.matmapparser.api;

import com.broadridge.brx.matmapparser.service.RepositoryScanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminAPI {

    private final RepositoryScanService repositoryScanService;

    @GetMapping("/repositories")
    public ResponseEntity<List<String>> listRepositories() {
        List<String> repositories = repositoryScanService.listRepositories();
        return ResponseEntity.ok(repositories);
    }

    @PostMapping("/repositories/{id}/scan")
    public ResponseEntity<Void> triggerScan(@PathVariable("id") Long id) {
        repositoryScanService.triggerScan(id);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}
