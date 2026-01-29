package com.broadridge.brx.matmapparser.service.impl;

import com.broadridge.brx.matmapparser.service.RepositoryScanService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class RepositoryScanServiceImpl implements RepositoryScanService {

    @Override
    public List<String> listRepositories() {
        // Temporary in-memory placeholder. Replace with DB-backed implementation.
        return new ArrayList<>();
    }

    @Override
    public void triggerScan(Long repositoryId) {
        // TODO: enqueue a scan job for the repository
    }
}
