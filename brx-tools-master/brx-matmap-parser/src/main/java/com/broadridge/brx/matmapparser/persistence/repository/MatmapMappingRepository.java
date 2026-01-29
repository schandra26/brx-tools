package com.broadridge.brx.matmapparser.persistence.repository;

import com.broadridge.brx.matmapparser.model.mapping.persistence.MatmapMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatmapMappingRepository extends JpaRepository<MatmapMapping, Long> {
}
