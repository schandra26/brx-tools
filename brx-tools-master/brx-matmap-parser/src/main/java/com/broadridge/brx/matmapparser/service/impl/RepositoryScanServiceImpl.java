package com.broadridge.brx.matmapparser.service.impl;

import com.broadridge.brx.matmapparser.model.repository.RepositorySource;
import com.broadridge.brx.matmapparser.model.scanjob.ScanJob;
import com.broadridge.brx.matmapparser.persistence.repository.RepositorySourceRepository;
import com.broadridge.brx.matmapparser.persistence.repository.ScanJobRepository;
import com.broadridge.brx.matmapparser.service.RepositoryScanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RepositoryScanServiceImpl implements RepositoryScanService {

    private final RepositorySourceRepository repositorySourceRepository;
    private final ScanJobRepository scanJobRepository;

    @Override
    public List<String> listRepositories() {
        return repositorySourceRepository.findByEnabled(true)
            .stream()
            .map(RepositorySource::getName)
            .toList();
    }

    @Override
    public void triggerScan(Long repositoryId) {
        repositorySourceRepository.findById(repositoryId)
            .ifPresentOrElse(
                repo -> {
                    ScanJob job = ScanJob.builder()
                        .repositoryId(repositoryId)
                        .status("PENDING")
                        .createdAt(Instant.now())
                        .build();
                    ScanJob savedJob = scanJobRepository.save(job);
                    log.info("[repository={}] Triggered scan job id={}", repositoryId, savedJob.getId());
                },
                () -> log.warn("[repository={}] Repository not found", repositoryId)
            );
    }
}
