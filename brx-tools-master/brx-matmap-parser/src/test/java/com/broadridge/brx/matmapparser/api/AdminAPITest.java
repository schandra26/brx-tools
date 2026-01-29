package com.broadridge.brx.matmapparser.api;

import com.broadridge.brx.matmapparser.service.RepositoryScanService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(AdminAPI.class)
class AdminAPITest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RepositoryScanService repositoryScanService;

    @Test
    void listRepositories() throws Exception {
        when(repositoryScanService.listRepositories()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/admin/repositories"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(repositoryScanService).listRepositories();
    }

    @Test
    void triggerScan() throws Exception {
        mockMvc.perform(post("/admin/repositories/123/scan"))
                .andExpect(status().isAccepted());

        verify(repositoryScanService).triggerScan(123L);
    }
}
