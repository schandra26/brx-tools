package com.broadridge.brx.matmapparser.service.impl;

import com.broadridge.brx.matmapparser.model.repository.RepositorySource;
import com.broadridge.brx.matmapparser.model.scanjob.ScanJob;
import com.broadridge.brx.matmapparser.persistence.repository.RepositorySourceRepository;
import com.broadridge.brx.matmapparser.persistence.repository.ScanJobRepository;
import com.broadridge.brx.matmapparser.service.RepositoryScanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RepositoryScanServiceImpl implements RepositoryScanService {

    private final RepositorySourceRepository repositorySourceRepository;
    private final ScanJobRepository scanJobRepository;

    @Override
    public List<RepositorySource> listRepositories() {
        log.info("Listing enabled repositories");
        return repositorySourceRepository.findByEnabled(true);
    }

    @Override
    public ScanJob triggerScan(Long repositoryId) {
        log.info("Triggering scan for repository id={}", repositoryId);
        ScanJob scanJob = ScanJob.builder()
                .repositoryId(repositoryId)
                .status("INITIATED")
                .build();
        return scanJobRepository.save(scanJob);
    }
}

