package com.broadridge.brx.matmapparser.persistence.repository;

import com.broadridge.brx.matmapparser.model.mapping.persistence.OpenAPIMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OpenAPIMappingRepository extends JpaRepository<OpenAPIMapping, Long> {
}
