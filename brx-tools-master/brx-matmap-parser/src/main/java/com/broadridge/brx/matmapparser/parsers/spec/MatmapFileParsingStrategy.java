package com.broadridge.brx.matmapparser.parsers.spec;

import com.broadridge.brx.matmapparser.finder.repositoryscan.model.SpecFileInput;
import com.broadridge.brx.matmapparser.model.Content;
import com.broadridge.brx.matmapparser.model.SpecFile;
import com.broadridge.brx.matmapparser.model.matmap.Translator;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.broadridge.brx.matmapparser.util.PathUtil.getFilenameFromFilePath;

@Service("matmap")
@RequiredArgsConstructor
@Slf4j
public class MatmapFileParsingStrategy implements SpecFileParsingStrategy {

    private final Unmarshaller unmarshaller;

    @Override
    @SneakyThrows
    public List<SpecFile> parseResources(final List<Resource> resources) {
        final List<SpecFile> specFiles = new ArrayList<>();
        for (final Resource resource : resources) {
            final Translator translator = parse(resource.getFilename(), resource.getInputStream());

            if (translator != null) {
                final SpecFile specFile = SpecFile.builder()
                        .filename(resource.getFilename())
                        .content(new Content(translator))
                        .build();
                specFiles.add(specFile);
            }
        }

        return specFiles;
    }

    @Override
    @SneakyThrows
    public List<SpecFile> parseSpecFileInputs(List<SpecFileInput> specFileInputs) {
        final List<SpecFile> specFiles = new ArrayList<>();

        for (final SpecFileInput specFileInput : specFileInputs) {
            final Translator translator = parse(specFileInput.getFilePath(), specFileInput.getContents().getInputStream());

            if (translator != null) {
                final SpecFile specFile = SpecFile.builder()
                        .filename(getFilenameFromFilePath(specFileInput.getFilePath()))
                        .lastCommitter(specFileInput.getLastCommitter())
                        .lastCommittedAt(specFileInput.getLastCommittedAt())
                        .content(new Content(translator))
                        .build();
                specFiles.add(specFile);
            }
        }

        return specFiles;
    }

    private Translator parse(final String filename, final InputStream inputStream) {
        try {
            return (Translator) unmarshaller.unmarshal(inputStream);
        } catch (JAXBException e) {
            log.warn("[matmapFileName={}] Unable to parse file", filename, e);
            return null;
        }
    }
}
