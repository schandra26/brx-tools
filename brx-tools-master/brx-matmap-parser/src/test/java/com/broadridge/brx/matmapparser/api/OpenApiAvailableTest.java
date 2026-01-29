package com.broadridge.brx.matmapparser.api;

import io.swagger.v3.parser.OpenAPIV3Parser;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Lightweight unit test to ensure the OpenAPI parser library is available on the classpath.
 * This avoids starting the full Spring context in CI where unrelated beans sometimes break startup.
 */
public class OpenApiAvailableTest {

    @Test
    void openApiParserAvailable() {
        OpenAPIV3Parser parser = new OpenAPIV3Parser();
        assertThat(parser).isNotNull();
    }

}
