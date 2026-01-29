package com.broadridge.brx.matmapparser.service.impl;

import com.broadridge.brx.matmapparser.service.RepositoryScanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class RepositoryScanServiceImpl implements RepositoryScanService {

    @Override
    public List<String> listRepositories() {
        log.info("Listing enabled repositories");
        return new ArrayList<>();
    }

    @Override
    public void triggerScan(Long repositoryId) {
        log.info("Triggering scan for repository id={}", repositoryId);
    }
}

