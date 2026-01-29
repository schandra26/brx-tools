package com.broadridge.brx.matmapparser.persistence.repository;

import com.broadridge.brx.matmapparser.model.scanjob.ScanJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScanJobRepository extends JpaRepository<ScanJob, Long> {
    List<ScanJob> findByStatus(String status);
    List<ScanJob> findByRepositoryId(Long repositoryId);
}
