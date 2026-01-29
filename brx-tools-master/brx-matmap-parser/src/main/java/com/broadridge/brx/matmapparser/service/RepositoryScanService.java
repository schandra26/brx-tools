package com.broadridge.brx.matmapparser.service;

import java.util.List;

public interface RepositoryScanService {

    List<String> listRepositories();

    void triggerScan(Long repositoryId);

}
