package com.broadridge.brx.matmapparser.config;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ApplicationConfigFileTest {

    @Test
    public void applicationYmlContainsPostgresUrl() throws Exception {
        InputStream is = getClass().getResourceAsStream("/application.yml");
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        String content = reader.lines().collect(Collectors.joining("\n"));
        assertTrue(content.contains("jdbc:postgresql"), "application.yml should contain a Postgres JDBC URL");
    }
}
