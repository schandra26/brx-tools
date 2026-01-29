package com.broadridge.brx.matmapparser.finder.repositoryscan.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.core.io.InputStreamResource;

import java.util.Date;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class SpecFileInput {

    private String filePath;
    private String lastCommitter;
    private Date lastCommittedAt;
    private InputStreamResource contents;

}
