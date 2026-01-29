package com.broadridge.brx.matmapparser.persistence.repository;

import com.broadridge.brx.matmapparser.model.repository.RepositorySource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RepositorySourceRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RepositorySourceRepository repositorySourceRepository;

    @Test
    void saveAndFindRepositorySource() {
        // Given
        RepositorySource source = RepositorySource.builder()
                .name("test-repo")
                .url("https://github.com/test/repo")
                .type("GITLAB")
                .enabled(true)
                .build();

        // When
        RepositorySource saved = repositorySourceRepository.save(source);
        entityManager.flush();
        entityManager.clear();

        // Then
        RepositorySource found = repositorySourceRepository.findById(saved.getId()).orElse(null);
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("test-repo");
        assertThat(found.getUrl()).isEqualTo("https://github.com/test/repo");
        assertThat(found.isEnabled()).isTrue();
    }

    @Test
    void findAllRepositories() {
        // Given
        RepositorySource source1 = RepositorySource.builder().name("repo1").url("url1").type("GITLAB").enabled(true).build();
        RepositorySource source2 = RepositorySource.builder().name("repo2").url("url2").type("GITHUB").enabled(true).build();
        repositorySourceRepository.save(source1);
        repositorySourceRepository.save(source2);
        entityManager.flush();

        // When
        var repos = repositorySourceRepository.findAll();

        // Then
        assertThat(repos).size().isGreaterThanOrEqualTo(2);
    }
}
