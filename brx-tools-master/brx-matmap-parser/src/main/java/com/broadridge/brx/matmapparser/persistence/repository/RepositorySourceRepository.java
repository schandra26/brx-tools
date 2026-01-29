package com.broadridge.brx.matmapparser.persistence.repository;

import com.broadridge.brx.matmapparser.model.repository.RepositorySource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepositorySourceRepository extends JpaRepository<RepositorySource, Long> {
    List<RepositorySource> findByEnabled(boolean enabled);
}
