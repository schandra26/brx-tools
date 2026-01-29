package com.broadridge.brx.matmapparser.persistence.repository;

import com.broadridge.brx.matmapparser.model.scanjob.ScanJob;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ScanJobRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ScanJobRepository scanJobRepository;

    @Test
    void saveScanJob() {
        // Given
        ScanJob job = ScanJob.builder()
                .repositoryId(1L)
                .status("PENDING")
                .createdAt(Instant.now())
                .build();

        // When
        ScanJob saved = scanJobRepository.save(job);
        entityManager.flush();
        entityManager.clear();

        // Then
        ScanJob found = scanJobRepository.findById(saved.getId()).orElse(null);
        assertThat(found).isNotNull();
        assertThat(found.getRepositoryId()).isEqualTo(1L);
        assertThat(found.getStatus()).isEqualTo("PENDING");
    }

    @Test
    void findPendingScanJobs() {
        // Given
        ScanJob job1 = ScanJob.builder().repositoryId(1L).status("PENDING").createdAt(Instant.now()).build();
        ScanJob job2 = ScanJob.builder().repositoryId(2L).status("COMPLETED").createdAt(Instant.now()).build();
        scanJobRepository.save(job1);
        scanJobRepository.save(job2);
        entityManager.flush();

        // When
        var pending = scanJobRepository.findByStatus("PENDING");

        // Then
        assertThat(pending).size().isGreaterThanOrEqualTo(1);
        assertThat(pending.stream().allMatch(j -> j.getStatus().equals("PENDING"))).isTrue();
    }
}
