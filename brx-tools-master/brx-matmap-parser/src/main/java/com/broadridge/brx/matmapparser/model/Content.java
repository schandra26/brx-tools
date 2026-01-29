package com.broadridge.brx.matmapparser.model;

import com.broadridge.brx.matmapparser.model.matmap.Translator;
import io.swagger.v3.oas.models.OpenAPI;

public class Content {
    private enum ContentType {
        MATMAP, OPENAPI
    }

    private final ContentType contentType;
    private final Object content;

    public Content(Translator translator) {
        this.contentType = ContentType.MATMAP;
        this.content = translator;
    }

    public Content(OpenAPI openAPI) {
        this.contentType = ContentType.OPENAPI;
        this.content = openAPI;
    }

    public Translator getMatmap() {
        if (!isTranslator()) {
            throw new IllegalStateException("Content is not a Translator");
        }
        return (Translator) content;
    }

    public OpenAPI getOpenAPI() {
        if (!isOpenAPI()) {
            throw new IllegalStateException("Content is not an OpenAPI spec");
        }
        return (OpenAPI) content;
    }

    private boolean isTranslator() {
        return contentType == ContentType.MATMAP;
    }

    private boolean isOpenAPI() {
        return contentType == ContentType.OPENAPI;
    }
}
