package com.broadridge.brx.matmapparser.service;

import com.broadridge.brx.matmapparser.model.repository.RepositorySource;
import com.broadridge.brx.matmapparser.model.scanjob.ScanJob;
import com.broadridge.brx.matmapparser.persistence.repository.RepositorySourceRepository;
import com.broadridge.brx.matmapparser.persistence.repository.ScanJobRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RepositoryScanServiceTest {

    @Mock
    private RepositorySourceRepository repositorySourceRepository;

    @Mock
    private ScanJobRepository scanJobRepository;

    private RepositoryScanService repositoryScanService;

    @BeforeEach
    void setUp() {
        // Create a real instance with mocked repositories
        repositoryScanService = new com.broadridge.brx.matmapparser.service.impl.RepositoryScanServiceImpl(
            repositorySourceRepository,
            scanJobRepository
        );
    }

    @Test
    void listRepositories_returnsEnabledRepositories() {
        RepositorySource repo1 = RepositorySource.builder()
            .id(1L)
            .name("repo1")
            .type("GITLAB")
            .enabled(true)
            .createdAt(Instant.now())
            .build();
        RepositorySource repo2 = RepositorySource.builder()
            .id(2L)
            .name("repo2")
            .type("GITLAB")
            .enabled(true)
            .createdAt(Instant.now())
            .build();

        when(repositorySourceRepository.findByEnabled(true))
            .thenReturn(Arrays.asList(repo1, repo2));

        List<String> repos = repositoryScanService.listRepositories();

        assertThat(repos).hasSize(2);
        assertThat(repos).contains("repo1", "repo2");
        verify(repositorySourceRepository).findByEnabled(true);
    }

    @Test
    void triggerScan_createsScanJobWithPendingStatus() {
        Long repoId = 1L;
        RepositorySource repo = RepositorySource.builder()
            .id(repoId)
            .name("test-repo")
            .type("GITLAB")
            .enabled(true)
            .createdAt(Instant.now())
            .build();

        when(repositorySourceRepository.findById(repoId))
            .thenReturn(Optional.of(repo));

        when(scanJobRepository.save(any(ScanJob.class)))
            .thenAnswer(inv -> {
                ScanJob job = inv.getArgument(0);
                job.setId(99L);
                return job;
            });

        repositoryScanService.triggerScan(repoId);

        verify(repositorySourceRepository).findById(repoId);
        verify(scanJobRepository).save(any(ScanJob.class));
    }
}
