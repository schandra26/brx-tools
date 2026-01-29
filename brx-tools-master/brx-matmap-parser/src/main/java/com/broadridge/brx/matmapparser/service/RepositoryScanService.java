package com.broadridge.brx.matmapparser.service;

import com.broadridge.brx.matmapparser.model.repository.RepositorySource;
import com.broadridge.brx.matmapparser.model.scanjob.ScanJob;
import java.util.List;

public interface RepositoryScanService {

    List<RepositorySource> listRepositories();

    ScanJob triggerScan(Long repositoryId);

}
