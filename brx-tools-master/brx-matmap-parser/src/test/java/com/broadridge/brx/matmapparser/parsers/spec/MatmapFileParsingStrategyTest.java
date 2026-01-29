package com.broadridge.brx.matmapparser.parsers.spec;

import com.broadridge.brx.matmapparser.finder.repositoryscan.model.SpecFileInput;
import com.broadridge.brx.matmapparser.model.SpecFile;
import com.broadridge.brx.matmapparser.model.matmap.Translator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatmapFileParsingStrategyTest {

    @Mock
    private Unmarshaller unmarshallerMock;
    @Mock
    private Resource resourceMock;
    @Mock
    private SpecFileInput specFileInputMock;
    @Mock
    private InputStream inputStreamMock;
    @Mock
    private InputStreamResource inputStreamResourceMock;
    @Mock
    private Translator translatorMock;

    @InjectMocks
    private MatmapFileParsingStrategy matmapFileParsingStrategy;

    @Test
    void parse_emptyResourceList() {
        List<Resource> emptyResources = Collections.emptyList();

        List<SpecFile> result = matmapFileParsingStrategy.parseResources(emptyResources);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verifyNoInteractions(unmarshallerMock);
    }

    @Test
    void parse_singleResource() throws Exception {
        List<Resource> resources = Collections.singletonList(resourceMock);

        when(resourceMock.getInputStream()).thenReturn(inputStreamMock);
        when(resourceMock.getFilename()).thenReturn("test.matmap");
        when(unmarshallerMock.unmarshal(inputStreamMock)).thenReturn(translatorMock);
        List<SpecFile> result = matmapFileParsingStrategy.parseResources(resources);

        assertNotNull(result);
        assertEquals(1, result.size());
        SpecFile specFile = result.getFirst();
        assertEquals("test.matmap", specFile.getFilename());
        assertEquals(translatorMock, specFile.getContent().getMatmap());

        verify(resourceMock).getInputStream();
        verify(resourceMock, times(2)).getFilename();
        verify(unmarshallerMock).unmarshal(inputStreamMock);
    }

    @Test
    void parse_whenUnmarshalFailsForResource() throws Exception {
        List<Resource> resources = Collections.singletonList(resourceMock);

        when(resourceMock.getInputStream()).thenReturn(inputStreamMock);
        when(unmarshallerMock.unmarshal(inputStreamMock)).thenThrow(new JAXBException("Unmarshal error"));

        List<SpecFile> result = matmapFileParsingStrategy.parseResources(resources);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void parse_withIOException() throws Exception {
        List<Resource> resources = Collections.singletonList(resourceMock);

        when(resourceMock.getInputStream()).thenThrow(new IOException());

        assertThrows(IOException.class, () -> matmapFileParsingStrategy.parseResources(resources));
    }

    @Test
    void parse_emptySpecFileInputList() {
        List<SpecFileInput> emptySpecFileInputs = Collections.emptyList();

        List<SpecFile> result = matmapFileParsingStrategy.parseSpecFileInputs(emptySpecFileInputs);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verifyNoInteractions(unmarshallerMock);
    }

    @Test
    void parse_singleSpecFileInput() throws Exception {
        List<SpecFileInput> specFileInputs = Collections.singletonList(specFileInputMock);

        when(specFileInputMock.getContents()).thenReturn(inputStreamResourceMock);
        when(inputStreamResourceMock.getInputStream()).thenReturn(inputStreamMock);
        when(specFileInputMock.getFilePath()).thenReturn("test.matmap");
        when(specFileInputMock.getLastCommitter()).thenReturn("John Doe");
        when(unmarshallerMock.unmarshal(inputStreamMock)).thenReturn(translatorMock);

        List<SpecFile> result = matmapFileParsingStrategy.parseSpecFileInputs(specFileInputs);

        assertNotNull(result);
        assertEquals(1, result.size());
        SpecFile specFile = result.getFirst();
        assertEquals("test.matmap", specFile.getFilename());
        assertEquals("John Doe", specFile.getLastCommitter());
        assertEquals(translatorMock, specFile.getContent().getMatmap());

        verify(inputStreamResourceMock).getInputStream();
        verify(specFileInputMock, times(2)).getFilePath();
        verify(unmarshallerMock).unmarshal(inputStreamMock);
    }

    @Test
    void parse_singleSpecFileInput_withIOException() throws Exception {
        List<SpecFileInput> specFileInputs = Collections.singletonList(specFileInputMock);

        when(specFileInputMock.getContents()).thenReturn(inputStreamResourceMock);
        when(inputStreamResourceMock.getInputStream()).thenThrow(new IOException());

        assertThrows(IOException.class, () -> matmapFileParsingStrategy.parseSpecFileInputs(specFileInputs));
    }

    @Test
    void parse_whenUnmarshalFailsForSpecFileInput() throws Exception {
        List<SpecFileInput> specFileInputs = Collections.singletonList(specFileInputMock);

        when(specFileInputMock.getContents()).thenReturn(inputStreamResourceMock);
        when(inputStreamResourceMock.getInputStream()).thenReturn(inputStreamMock);
        when(unmarshallerMock.unmarshal(inputStreamMock)).thenThrow(new JAXBException("Unmarshal error"));

        List<SpecFile> result = matmapFileParsingStrategy.parseSpecFileInputs(specFileInputs);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}