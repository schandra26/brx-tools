package com.broadridge.brx.matmapparser.api;

import com.broadridge.brx.matmapparser.model.mapping.domain.Mapping;
import com.broadridge.brx.matmapparser.service.QueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(QueryAPI.class)
class QueryAPITest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private QueryService queryService;

    private List<Mapping> mockMappings;

    @BeforeEach
    void setUp() {
        mockMappings = List.of(
                Mapping.builder()
                        .filename("test1.yaml")
                        .sourceField("source1")
                        .targetField("target1")
                        .build(),
                Mapping.builder()
                        .filename("test2.yaml")
                        .sourceField("source2")
                        .targetField("target2")
                        .build()
        );
    }

    @Test
    void getMappingsJson_withNoFilters() throws Exception {
        when(queryService.getFilteredMappings(null, null, null)).thenReturn(mockMappings);

        mockMvc.perform(get("/api/mappings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getMappingsJson_withSourceFilter() throws Exception {
        String sourceFilter = "source1";
        List<Mapping> filteredMappings = List.of(mockMappings.getFirst());
        when(queryService.getFilteredMappings(null, sourceFilter, null)).thenReturn(filteredMappings);

        mockMvc.perform(get("/api/mappings")
                        .param("source", sourceFilter))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getMappingsJson_withBothFilters() throws Exception {
        String sourceFilter = "source2";
        String targetFilter = "target2";
        List<Mapping> filteredMappings = List.of(mockMappings.get(1));
        when(queryService.getFilteredMappings(null, sourceFilter, targetFilter)).thenReturn(filteredMappings);

        mockMvc.perform(get("/api/mappings")
                        .param("source", sourceFilter)
                        .param("target", targetFilter))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }
}