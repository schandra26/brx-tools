package com.broadridge.brx.matmapparser.service;

import com.broadridge.brx.matmapparser.persistence.repository.RepositorySourceRepository;
import com.broadridge.brx.matmapparser.persistence.repository.ScanJobRepository;
import com.broadridge.brx.matmapparser.service.impl.RepositoryScanServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RepositoryScanServiceImplTest {

    @Mock
    private RepositorySourceRepository repositorySourceRepository;

    @Mock
    private ScanJobRepository scanJobRepository;

    private RepositoryScanService repositoryScanService;

    @BeforeEach
    void setUp() {
        repositoryScanService = new RepositoryScanServiceImpl(
            repositorySourceRepository,
            scanJobRepository
        );
    }

    @Test
    void triggerScan_callsRepositoryScanService() {
        repositoryScanService.triggerScan(1L);
        verify(repositorySourceRepository).findById(1L);
    }

    @Test
    void listRepositories_delegatesToRepository() {
        repositoryScanService.listRepositories();
        verify(repositorySourceRepository).findByEnabled(true);
    }
}
